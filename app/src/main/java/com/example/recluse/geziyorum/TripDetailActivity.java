package com.example.recluse.geziyorum;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.recluse.geziyorum.db.helper.LocalDbHelper;
import com.example.recluse.geziyorum.models.LocationModel;
import com.example.recluse.geziyorum.models.TripModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final Gson gson = new Gson();

    private GoogleMap mMap;
    private TripModel trip;

    LocalDbHelper localDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tripDetailMap);

        mapFragment.getMapAsync(this);

        localDbHelper = new LocalDbHelper(this);

        Bundle extras = this.getIntent().getExtras();

        String tripJson = (String) extras.get("trip");
        this.trip = gson.fromJson(tripJson,TripModel.class);






    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        ArrayList<LocationModel> locationModels = localDbHelper.GetLocations(trip.getId());
        PrintLinesToMap(locationModels);
    }

    private Location LocationModelToLocation(LocationModel locationModel){
        Location location = new Location("Last Location");
        location.setLongitude(locationModel.getLongitude());
        location.setLatitude(locationModel.getLatitude());
        return location;
    }

    private ArrayList<Location> GetLocationsList(ArrayList<LocationModel> locationModels){
        ArrayList<Location> locations = new ArrayList<>();
        for(LocationModel locationModel : locationModels){
            locations.add(LocationModelToLocation(locationModel));
        }
        return locations;
    }

    private void PrintLinesToMap(ArrayList<LocationModel> locations){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()), 13));

        for(int i=1 ; i<locations.size() ; i++){
            LatLng src = new LatLng(locations.get(i-1).getLatitude(),locations.get(i-1).getLongitude());
            LatLng dst = new LatLng(locations.get(i).getLatitude(),locations.get(i).getLongitude());

            Polyline polyline = mMap.addPolyline(new PolylineOptions().add(src,dst).width(3).color(Color.RED).geodesic(true));
        }
    }
}
