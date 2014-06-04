package com.androidhuman.example.mapsv2example;
import com.androidhuman.example.mapsv2example.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.*;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	private GoogleMap mMap;
	private boolean isChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.609855, 126.997137), 18));
		
		mMap.addMarker(new MarkerOptions()
		.title("Kookmin University")
		.snippet("Building No.7")
        .position(new LatLng(37.609855, 126.997137)));
		
		mMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				isChecked = false;
			}
		});
		
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				if(isChecked){
					Intent indoor = new Intent(MainActivity.this, indoorMain.class);
					startActivity(indoor);
				}else{
					isChecked = true;
				}
				return false;
			}
		});

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
				if(isChecked){
					Intent indoor = new Intent(MainActivity.this, indoorMain.class);
					startActivity(indoor);
				}
			}
		});
	}
}