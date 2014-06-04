package com.androidhuman.example.mapsv2example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;


@SuppressLint("NewApi") public class tab4 extends FragmentActivity implements OnClickListener{
	private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;
	private BluetoothAdapter btAdapter;
	private BluetoothStateReceiver btReceiver;
	private Button problemButton;
	private String carBtAddress;
	private TextView carTextView;
	private TextView locationTextView;
	private MapFragment mapFragment;
	private GoogleMap map;
	//radar
	
	public static final String CAR_PREFERENCES = "be.ndusart.carfinder.MainActivity.CAR_PREFERENCES";
	public static final String CAR_BT_ADDRESS_KEY = "carBtAddressKey";
	public static final String CAR_LAST_LATITUDE_KEY = "carBtLatitudeKey";
	public static final String CAR_LAST_LONGITUDE_KEY = "carBtLongitudeKey";
	public static final String CAR_LAST_POSITION_STREET_KEY = "carBtStreetKey";
	private static final int REQUEST_ENABLE_BT = 3;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carlocation);
		
		carBtAddress = null;
		
		problemButton = (Button)findViewById(R.id.problemButton);
		problemButton.setOnClickListener(this);
		
		carTextView = (TextView)findViewById(R.id.carLabel);
		locationTextView = (TextView)findViewById(R.id.locationLabel);
		
		mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		/*
		setContentView(R.layout.radar);
        mRadar = (RadarView) findViewById(R.id.radar);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
          */   
		if( map!=null ) {
			map.setPadding(10, 10, 10, 10);
			map.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
			map.setMyLocationEnabled(true);
		}
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(btReceiver, filter);
		
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(btReceiver, filter);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if( btAdapter == null ) {
			Toast.makeText(this, "Your device does not support bluetooth !", Toast.LENGTH_LONG).show();
		} else {
			SharedPreferences preferences = getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
			carBtAddress = preferences.getString(CAR_BT_ADDRESS_KEY, null);
		}
	}
	
	public static String getCarBluetoothAddress(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
		return preferences.getString(CAR_BT_ADDRESS_KEY, null);
	}
	
	public static float getLastLatitude(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
		return preferences.getFloat(CAR_LAST_LATITUDE_KEY, 0.0f);
	}
	
	public static float getLastLongitude(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
		return preferences.getFloat(CAR_LAST_LONGITUDE_KEY, 0.0f);
	}
	
	public static boolean hasLocation(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
		
		return (preferences.contains(CAR_LAST_LATITUDE_KEY) && preferences.contains(CAR_LAST_LONGITUDE_KEY));
	}
	
	public static String getLastStreet(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
		return preferences.getString(CAR_LAST_POSITION_STREET_KEY, null);
	}
	
	public static void updateLastStreet(String street, Context context) {
		SharedPreferences.Editor edit = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE).edit();
		edit.putString(CAR_LAST_POSITION_STREET_KEY, street);
		edit.commit();
	}
	
	public static void removeLastStreet(Context context) {
		SharedPreferences.Editor edit = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE).edit();
		edit.remove(CAR_LAST_POSITION_STREET_KEY);
		edit.commit();
	}
	
	public static void updatePosition(float latitude, float longitude, Context context) {
		SharedPreferences.Editor edit = context.getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE).edit();
		edit.putFloat(CAR_LAST_LATITUDE_KEY, latitude);
		edit.putFloat(CAR_LAST_LONGITUDE_KEY, longitude);
		edit.commit();
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		if( btAdapter!= null ) {
			if( !btAdapter.isEnabled() ) {
				problemButton.setText("Turn on Bluetooth");
				problemButton.setVisibility(View.VISIBLE);
			} else if( !isCarConfigured() ) {
				problemButton.setText("Configure your Device");
				problemButton.setVisibility(View.VISIBLE);
			} else {
				showCarName();
			}
		}
		
		if( hasLocation(this) ) {
			/*
			Intent i = getIntent();
		      int latitude = (int)(i.getFloatExtra("latitude", 0) * GeoUtils.MILLION);
		      int longitude = (int)(i.getFloatExtra("longitude", 0) * GeoUtils.MILLION);
		      mRadar.setTarget(latitude, longitude);
		      mRadar.setDistanceView((TextView) findViewById(R.id.distance));
			*/
			float latitude = getLastLatitude(this);
			float longitude = getLastLongitude(this);
			String street = getLastStreet(this);
			locationTextView.setText("Last location: ("+latitude+","+longitude+")");
			locationTextView.setVisibility(View.GONE);
			if( street==null || street.length()==0 ) {
				if(Geocoder.isPresent()) {
					Location last = new Location("com.example");
					last.setLatitude(latitude);
					last.setLongitude(longitude);
					(new GetAddressTask()).execute(last);
				}
			} else {
				locationTextView.setText("Last location: "+street);
			}
			
			if( map != null ) {
				map.clear();
				LatLng carPosition = new LatLng(latitude, longitude);
				Location location;
				map.addMarker(new MarkerOptions().position(carPosition)).setAnchor((float)carPosition.latitude, (float)carPosition.longitude);
				map.moveCamera(CameraUpdateFactory.newLatLng(carPosition));
			}
		} else {
			locationTextView.setVisibility(View.GONE);
		}
		
		btReceiver = new BluetoothStateReceiver();
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(btReceiver, filter);
		
		filter = new IntentFilter(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION);
		registerReceiver(btReceiver, filter);
		Log.d("receive","111111111111110");
	}
	
	@Override
	protected void onStop() {
		unregisterReceiver(btReceiver);
		btReceiver = null;
		
		problemButton.setVisibility(View.VISIBLE);
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if( v == problemButton && btAdapter != null ) {
			if( !btAdapter.isEnabled() ) {
				enableBluetooth();
			} else {
				configureCar();
			}
		}
	}
	
	private void enableBluetooth() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	
	private boolean isCarConfigured() {
		boolean configured = false;
		
		if( btAdapter!=null && btAdapter.isEnabled() && carBtAddress != null && carBtAddress.length()>0 ) {
			for (BluetoothDevice device : btAdapter.getBondedDevices()) {
				if( device.getAddress().equals(carBtAddress) ) {
					configured = true;
					break;
				}
			}
		}
		
		return configured;
	}
	
	private void showCarName() {
		if( btAdapter!=null && btAdapter.isEnabled() && carBtAddress != null && carBtAddress.length()>0 ) {
			for (BluetoothDevice device : btAdapter.getBondedDevices()) {
				if( device.getAddress().equals(carBtAddress) ) {
					carTextView.setText("Your device is set to \""+device.getName()+"\"");
					break;
				}
			}
		}
	}
	
	private void configureCar() {
		if( btAdapter==null || !btAdapter.isEnabled() )
			return;
		
		ArrayList<String> btNames = new ArrayList<String>();
		ArrayList<String> btAddresses = new ArrayList<String>();
		
		for (BluetoothDevice device : btAdapter.getBondedDevices()) {
			btNames.add(device.getName());
			btAddresses.add(device.getAddress());
		}
		
		if(btNames.size()>0){
			
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose your device");
		builder.setItems(btNames.toArray(new String[0]), new DialogClickListener(btAddresses));
		builder.create().show();
	}
	
	private class BluetoothStateReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
			/*
			ArrayList<Integer> btRssi = new ArrayList<Integer>();
			
			for (BluetoothDevice device : btAdapter.getBondedDevices()) {
				btRssi.add(intent.getIntExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
			}
			
			intent.putExtra("btRssi", btRssi);
			startActivity(intent);
			*/
			if( state == BluetoothAdapter.STATE_ON ) {
				if( !isCarConfigured() ) {
					problemButton.setText("Configure your device");
					problemButton.setVisibility(View.VISIBLE);
				} else {
					problemButton.setVisibility(View.VISIBLE);
				}
			} else {
				problemButton.setText("Turn on Bluetooth");
				problemButton.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private class DialogClickListener implements DialogInterface.OnClickListener {
		
		private ArrayList<String> btAddresses;
		
		public DialogClickListener(ArrayList<String> addresses) {
			btAddresses = new ArrayList<String>(addresses);
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			carBtAddress = btAddresses.get(which);
			SharedPreferences preferences = getSharedPreferences(CAR_PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(CAR_BT_ADDRESS_KEY, carBtAddress);
			editor.commit();
			
			problemButton.setVisibility(View.VISIBLE);
			
			showCarName();
		}
		
	}
	
	
	private class GetAddressTask extends AsyncTask<Location, Void, String> {

		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(tab4.this);
			Location loc = params[0];
			List<Address> addresses = null;
			
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				return null;
			}
			
			if( addresses != null && addresses.size() > 0 ) {
				Address addr = addresses.get(0);
				String addressText = String.format(
									"%s, %s, %s",
									// If there's a street address, add it
									addr.getMaxAddressLineIndex() > 0 ?
											addr.getAddressLine(0) : "",
									// Locality is usually a city
									addr.getLocality(),
									// The country of the address
									addr.getCountryName());
				updateLastStreet(addressText, tab4.this);
				return addressText;
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			if( result != null && result.length() > 0 ) {
				locationTextView.setText("Last location: "+result);
			}
		}
	}
}
