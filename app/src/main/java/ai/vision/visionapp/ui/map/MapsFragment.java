package ai.vision.visionapp.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.location.Location;

//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.location.LocationServices;

import ai.vision.visionapp.R;
import ai.vision.visionapp.databaseHelper;

public class MapsFragment extends Fragment{

    private SupportMapFragment map;
    private databaseHelper veicles;
    //private FusedLocationProviderClient client;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng PTcenter = new LatLng(39.7, -8.13);

            //client = LocationServices.getFusedLocationProviderClient(this.);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Aqui"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(PTcenter));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_maps, container, false);
        View v = inflater.inflate(R.layout.fragment_maps, container, false);


        //map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        //map.getMapAsync(callback);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    public void setVeiclesOnMap(){
        LatLng coord= new LatLng(40,-8);
        this.veicles.getveiclelat(coord.latitude);
        this.veicles.getveiclelon();


    }
}