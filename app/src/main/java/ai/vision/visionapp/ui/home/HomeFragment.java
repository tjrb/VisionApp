package ai.vision.visionapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ai.vision.visionapp.R;
import ai.vision.visionapp.databaseHelper;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private databaseHelper veicle;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);*/
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        this.veicle=new databaseHelper(getContext());
        Button resetdb = (Button) root.findViewById(R.id.resetdb);
        resetdb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                veicle.resetdb();
            }
            });

        int veiclesNum = this.veicle.entries();
        TextView tv =(TextView)root.findViewById(R.id.text_home);
        tv.setText("Numero de carros:"+veiclesNum);


        LineChart graph = (LineChart) root.findViewById(R.id.graph);


        ArrayList<Long> time = this.veicle.getveicletime();

        ArrayList<Entry> series = new ArrayList<>();
        Map<Long, Integer> freq = new HashMap<Long, Integer>();

        //for(long i =time.get(0);i<(int)(System.currentTimeMillis()/1000);i=i+600){
        long last=time.get(0);
        int count=0;
        for (int i=0;i< time.size();i++){
            if(last==time.get(i)){
                count++;

            }else{
                freq.put(last, count);
                count=1;
                last=time.get(i);
            }
        }
        freq.put(last,count);

        Iterator<Long> it = freq.keySet().iterator();
        while(it.hasNext()){
            long x=it.next();
            int y=freq.get(x);
            series.add(new Entry(((x-1621978755)/3600f),(float)y));
            Log.d("freq:",x+">>"+y);
        }




        //}


        /*double x=0,y;

        for (int i=0;i<500;i++){
            x=x+0.1;
            y=Math.sin(x);
            series.add(new Entry((float)x, (float) y));
        }*/

        Collections.sort(series,new EntryXComparator());

        LineDataSet dataset =new LineDataSet(series,"veicles");
        dataset.setLineWidth(1.5f);
        dataset.setDrawCircles(false);
        dataset.setMode(LineDataSet.Mode.LINEAR);
        dataset.setDrawFilled(true);

        LineData data = new LineData(dataset);
        graph.clear();
        graph.setData(data);

        return root;
    }

}