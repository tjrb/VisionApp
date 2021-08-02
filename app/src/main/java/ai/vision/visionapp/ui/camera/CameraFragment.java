package ai.vision.visionapp.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ai.vision.visionapp.R;
import ai.vision.visionapp.databaseHelper;


public class CameraFragment extends Fragment implements CvCameraViewListener2
    {

    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "CameraViewFragment";
    public BaseLoaderCallback mLoaderCallback;
    //private static List<String> classNames;
    private static List<Scalar> colors = new ArrayList<>();
    private Net net;
    private static final String[] classNames = {"background",
            "aviao", "bicicleta", "passaro", "barco",
            "garrafa", "autocarro", "carro", "gato", "cadeira",
            "vaca", "mesa de jantar", "cao", "cavalo",
            "mota", "pessoa", "plata",
            "ovelha", "sofa", "comboio", "monitor"};
    private databaseHelper veicle;
    private LocationManager location;


    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java3");
        }
    }
/*
@Override
public void onStart() {
    super.onStart();
    Net net = Dnn.readNetFromDarknet("src/main/res/dlmodels/yolov3-tiny.cfg","src/main/res/dlmodels/yolov3-tiny.weights");
}*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        //JavaCamera2View camera2View = root.findViewById(R.id.CameraView);

        mLoaderCallback = new BaseLoaderCallback(this.getActivity())
            {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, "OpenCV loaded Successfully");
                        mOpenCvCameraView.enableView();
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }

            };
        mOpenCvCameraView = (CameraBridgeViewBase) root.findViewById(R.id.CameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        /*classNames = readLabels("labels.txt", getContext());
        for (int i = 0; i < classNames.size(); i++)
            colors.add(randomColor());*/

        return root;
    }

    public CameraFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        /*super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this.getActivity(),
                mLoaderCallback);*/
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.getContext(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // TODO Auto-generated method stub
        loadNet();
    }

    @Override
    public void onCameraViewStopped() {
        // TODO Auto-generated method stub

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // TODO Auto-generated method stub
        Mat frame = inputFrame.rgba();
        Core.rotate(frame, frame, Core.ROTATE_90_CLOCKWISE);

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

        Size frame_size = new Size(300, 300);
        Scalar mean = new Scalar(127.5, 127.5, 127.5);

        //Imgproc.resize(frame,frame,frame_size);
        Mat blob = Dnn.blobFromImage(frame, 1.0 / 127.5, frame_size, mean, false, false);
        //save_mat(blob);
        net.setInput(blob);

        //List<Mat> result = new ArrayList<>();
        //List<String> outBlobNames = net.getUnconnectedOutLayers(); //getUnconnectedOutLayersNames();

        //net.forward(result, outBlobNames);
        Mat detections = net.forward();

        float confThreshold = 0.6f;
        int cols = frame.cols();
        int rows = frame.rows();
        detections = detections.reshape(1, (int) detections.total() / 7);
        /**for (int i = 0; i < result.size(); ++i) {
         // each row is a candidate detection, the 1st 4 numbers are
         // [center_x, center_y, width, height], followed by (N-4) class probabilities
         Mat level = result.get(i);
         for (int j = 0; j < level.rows(); ++j) {
         Mat row = level.row(j);
         Mat scores = row.colRange(5, level.cols());
         Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
         float confidence = (float) mm.maxVal;
         Point classIdPoint = mm.maxLoc;
         if (confidence > confThreshold) {

         int centerX = (int) (row.get(0, 0)[0] * frame.cols());
         int centerY = (int) (row.get(0, 1)[0] * frame.rows());
         int width = (int) (row.get(0, 2)[0] * frame.cols());
         int height = (int) (row.get(0, 3)[0] * frame.rows());

         int left = (int) (centerX - width * 0.5);
         int top =(int)(centerY - height * 0.5);
         int right =(int)(centerX + width * 0.5);
         int bottom =(int)(centerY + height * 0.5);

         Point left_top = new Point(left, top);
         Point right_bottom=new Point(right, bottom);
         Point label_left_top = new Point(left, top-5);
         DecimalFormat df = new DecimalFormat("#.##");

         int class_id = (int) classIdPoint.x;
         String label= classNames.get(class_id) + ": " + df.format(confidence);
         Scalar color= colors.get(class_id);

         Imgproc.rectangle(frame, left_top,right_bottom , color, 3, 2);
         Imgproc.putText(frame, label, label_left_top, Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 4);
         Imgproc.putText(frame, label, label_left_top, Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 255), 2);
         }
         }
         }**/
        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > 0.6) {
                int classId = (int) detections.get(i, 1)[0];
                int left = (int) (detections.get(i, 3)[0] * cols);
                int top = (int) (detections.get(i, 4)[0] * rows);
                int right = (int) (detections.get(i, 5)[0] * cols);
                int bottom = (int) (detections.get(i, 6)[0] * rows);
                // Draw rectangle around detected object.
                Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom),
                        new Scalar(0, 255, 0));
                int conf = (int) (confidence * 100);
                String label = classNames[classId] + ": " + conf + "%";
                if (classId==7){
                    veiclesHandleUpdate();
                }

                int[] baseLine = new int[1];
                Size labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_TRIPLEX, 1, 4, baseLine);
                // Draw background for label.
                Imgproc.rectangle(frame, new Point(left, top - labelSize.height),
                        new Point(left + labelSize.width, top + baseLine[0]),
                        new Scalar(255, 255, 255), Core.FILLED);
                // Write class name and confidence.
                Imgproc.putText(frame, label, new Point(left, top),
                        Core.FONT_HERSHEY_TRIPLEX, 1, new Scalar(0, 0, 0));
                Log.d("ONCAMERAFRAME", "pass");
            }

        }
        //Imgproc.putText(frame,"net frame",new Point(500,500),Core.FONT_HERSHEY_SIMPLEX,2,new Scalar(0,0,0));
        //Log.d("ONCAMERAFRAME","pass");
        Core.rotate(frame, frame, Core.ROTATE_90_COUNTERCLOCKWISE);
        return frame;
    }

    public boolean loadNet() {

        String proto = getAssetsFile("MobileNetSSD_deploy.prototxt.txt", getContext());
        String caffemodel = getAssetsFile("MobileNetSSD_deploy.caffemodel", getContext());
        Log.d("path: ", proto);

        net = Dnn.readNetFromCaffe(proto, caffemodel);
        return true;
    }

    private static String getAssetsFile(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (
                IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }

   /* private List<String> readLabels(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream;
        List<String> labelsArray = new ArrayList<>();
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            Scanner fileScanner = new Scanner(new File(outFile.getAbsolutePath())).useDelimiter("\n");
            String label;
            while (fileScanner.hasNext()) {
                label = fileScanner.next();
                labelsArray.add(label);
            }
            fileScanner.close();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to read labels!");
        }
        return labelsArray;
    }*/

    private Scalar randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return new Scalar(r, g, b);
    }

    private void veiclesHandleUpdate() {
         Long time = (System.currentTimeMillis()/1000);

        //Timestamp timestamp = new Timestamp(time);
        //Log.d("Timestamp",time+">>"+timestamp.toString());
        this.veicle= new databaseHelper(getContext());
        this.location = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }
        Log.d("Location providers:", String.valueOf(this.location.getAllProviders()));
         Location locationGps ;
        double lat = 40;
        double lon = -8;





        this.veicle.insertVeicle(time, lat, lon);

    }


    }

