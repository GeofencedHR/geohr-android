package com.epgeotrack.app.ep_geo_tracking;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;

public class WorkPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        HashMap<LatLng, String> locations = new HashMap<>();
        locations.put(new LatLng(6.9336686, 79.850047), "Colombo Fort");
        locations.put(new LatLng(6.9281946, 79.8446079), "Shangri-La Hotel, Colombo");
        locations.put(new LatLng(6.920566, 79.856158), "Hilton Residencies, Colombo");
        locations.put(new LatLng(6.9177047, 79.8630878), "Union Chemists, Colombo");

        // Add a marker in Sydney and move the camera
        for (LatLng latLng : locations.keySet()) {
            mMap.addMarker(generateMarker(latLng, locations.get(latLng)));
            builder.include(latLng);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 150);
        mMap.moveCamera(cameraUpdate);
        googleMap.animateCamera(cameraUpdate);

    }

    private MarkerOptions generateMarker(LatLng latLng, String title) {
        TextView text = new TextView(getApplicationContext());
        text.setText(title);
        text.setTextColor(Color.parseColor("#ffffff"));
        text.setPadding(5, 5, 5, 5);
        IconGenerator generator = new IconGenerator(getApplicationContext());
        generator.setBackground(getApplicationContext().getDrawable(R.drawable.amu_bubble_mask));
        generator.setContentView(text);
        Bitmap icon = generator.makeIcon();

        return new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
