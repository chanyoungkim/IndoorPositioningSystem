/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidhuman.example.mapsv2example;

import java.util.Date;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class CarDetector extends BroadcastReceiver implements LocationListener {
	
	private static int LOCATION_FRESH_LIMIT = 10000; // only consider location from less than 10 seconds
	private static int MAX_WAIT_LOCATION_UPDATE = 15000; // accept an update until 15 sec after connection loss
	private long disconnectTime;
	private LocationManager locationManager;
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String car = tab4.getCarBluetoothAddress(context);
		
		if( car==null || car.length()==0 || intent.getAction()!=BluetoothDevice.ACTION_ACL_DISCONNECTED )
			return;
		
		BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		if( device != null && device.getAddress().equals(car) ) {
			Toast.makeText(context, "Going out of car, storing location...", Toast.LENGTH_LONG).show();
			tab4.removeLastStreet(context);
			updatePosition(context);
		}
	}
	
	private void updatePosition(Context context) {
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		mContext = context;
		
		Location lastNetworkPosition = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location lastGPSPosition = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location lastPassivePosition = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		long now = new Date().getTime();
		
		double latitude=0.0, longitude=0.0;
		
		if ( lastGPSPosition != null && (now - lastGPSPosition.getTime()) < LOCATION_FRESH_LIMIT ) {
			latitude = lastGPSPosition.getLatitude();
			longitude = lastGPSPosition.getLongitude();
		} else if ( lastNetworkPosition != null && (now - lastNetworkPosition.getTime()) < LOCATION_FRESH_LIMIT ) {
			latitude = lastNetworkPosition.getLatitude();
			longitude = lastNetworkPosition.getLongitude();
		} else if ( lastPassivePosition != null && (now - lastPassivePosition.getTime()) < LOCATION_FRESH_LIMIT ) {
			latitude = lastPassivePosition.getLatitude();
			longitude = lastPassivePosition.getLongitude();
		}
		
		if( latitude != 0.0 || longitude != 0.0 ) {
			tab4.updatePosition((float)latitude, (float)longitude, context);
		} else {
			disconnectTime = now;
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1.0f, this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1.0f, this);
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 1.0f, this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		String provider = location.getProvider();
		
		if( (location.getTime() - disconnectTime) > MAX_WAIT_LOCATION_UPDATE )
		{
			// update took too long, discard and stop updates
			locationManager.removeUpdates(this);
			return;
		}
		
		if( provider == LocationManager.GPS_PROVIDER ) {
			// always use the new position if from GPS
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			tab4.updatePosition((float)latitude, (float)longitude, mContext);
			locationManager.removeUpdates(this); // GPS is enough accurate, stop updates
			mContext = null;
		} else if( provider == LocationManager.NETWORK_PROVIDER ) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			tab4.updatePosition((float)latitude, (float)longitude, mContext);
		} else if( provider == LocationManager.PASSIVE_PROVIDER ) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			tab4.updatePosition((float)latitude, (float)longitude, mContext);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
