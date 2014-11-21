package com.impecabel.betherein;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationHelper /* extends Thread */
implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	private static final long REQUEST_NORMAL_FREQUENCY = 5000;

	private static final long REQUEST_FAST_FREQUENCY = 1000;

	private LocationListener locationListener;

	private LocationClient locationClient;

	private LocationRequest locationRequest;

	private Context contextActivity;

	private Location deviceLocation;

	public interface onLocationUpdate {
		public void locationUpdated(Location newLocation);
	}

	private onLocationUpdate callback;

	public LocationHelper(Context context, onLocationUpdate callback) {
		this.contextActivity = context;
		this.callback = callback;
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
			locationClient = new LocationClient(context, this, this);
			if (!locationClient.isConnected() && !locationClient.isConnecting()) {
				locationClient.connect();
			}
		}
	}

	/*
	 * @Override public void run() {
	 * 
	 * if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(contextActivity)
	 * == ConnectionResult.SUCCESS) { locationClient = new
	 * LocationClient(contextActivity, this, this); if
	 * (!locationClient.isConnected() && !locationClient.isConnecting()) {
	 * locationClient.connect(); } } }
	 */

	public Location getDeviceLocation() {

		if (locationClient.isConnected()) {
			return deviceLocation;
		} else {
			return null;
		}
	}

	private void setDeviceLocation(Location deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	@Override
	public void onConnected(Bundle dataBundle) {

		this.setDeviceLocation(locationClient.getLastLocation());
		this.setLocationListener(this);
		this.requestLocationUpdates();

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(
						(Activity) contextActivity, 9000);
			} catch (IntentSender.SendIntentException e) {
				Log.e("LocationClient", "LocationClient ConnectionFailed.");
				Log.e("LocationClient", e.toString());
			}
		} else {
			Log.e("LocationClient", "LocationClient ConnectionFailed ("
					+ connectionResult.getErrorCode() + ").");
		}
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		this.setDeviceLocation(locationClient.getLastLocation());
		if (callback != null) {
			callback.locationUpdated(newLocation);
		}
	}

	@Override
	public void onDisconnected() {
		Log.d("LocationClient", "LocationClient Disconnected");
	}

	public void connect() {
		locationClient.connect();
	}

	public void disconnect() {
		locationClient.disconnect();
	}

	public boolean isConnected() {
		return locationClient.isConnected();
	}

	public void requestLocationUpdates() {
		if (locationRequest == null) {
			locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(REQUEST_NORMAL_FREQUENCY);
			locationRequest.setFastestInterval(REQUEST_FAST_FREQUENCY);
		}

		locationClient
				.requestLocationUpdates(locationRequest, locationListener);
	}

	public void removeLocationUpdates() {
		locationClient.removeLocationUpdates(locationListener);
	}

	public void reset() {
		if (isConnected()) {
			removeLocationUpdates();
			disconnect();
		}

		locationClient = null;
		locationListener = null;
		locationRequest = null;
	}
}
