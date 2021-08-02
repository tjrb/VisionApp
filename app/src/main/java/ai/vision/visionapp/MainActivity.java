package ai.vision.visionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.dnn.Layer;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity
    {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*SharedPreferences sp=getPreferences(MODE_PRIVATE);
        boolean chk_theme = sp.getBoolean("Theme",false);
        if (chk_theme) {
            setTheme(R.style.DarkTheme);
            //getActivity().set
            //getContext().getTheme().applyStyle(R.style.DarkTheme,true);
        } else {
            setTheme(R.style.LightTheme);
            //getContext().getTheme().applyStyle(R.style.LightTheme,true);
            //getListView().setBackgroundColor(Color.parseColor("#181818"));
            Log.d("themeChangeMain:","yes"+chk_theme);
        }*/

        setContentView(R.layout.activity_main);
        /**permitions**/
         /** Camera**/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { }
        else {ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);}
         /** gps **/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { }
        else {ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);}

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) { }
        else {ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);}
         /** Internet **/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) { }
        else { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 300);}

        // layout

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_map,R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();


        //ArrayList classNames = loader(getAssets().);
    }

    public ArrayList<String> loader(String fpath) {
        ArrayList<String> result = new ArrayList<>();

        try (FileReader f = new FileReader(fpath)) {
            StringBuffer sb = new StringBuffer();
            while (f.ready()) {
                char c = (char) f.read();
                if (c == '\n') {
                    result.add(sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                result.add(sb.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("loadCoco:","done"+result);
        return result;
    }
    }
