package com.impecabel.betherein;

import android.app.Activity;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.impecabel.betherein.FetchDirectionsTask.OnFinish;
import com.impecabel.betherein.GetAddressTask.onFinish;
import com.impecabel.betherein.LocationHelper.onLocationUpdate;
import com.melnykov.fab.FloatingActionButton;

public class DestinationListActivity extends Activity implements
		SensorEventListener {
	DestinationListAdapter exAdpt;
	ExpandableListView exList;

	private LocationHelper mLocationHelper;
	private TextView tvLocation;
	private TextView tvAddress;
	
	private GetAddressTask get_address;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private float[] accelVals = new float[3];
	private float[] compassVals = new float[3];
	private float[] mOrientation = new float[3];
	private float mCurrentDegree = 0f;
	
	private float heading = 0f;
	private float destBearing = 0f;
	private GeomagneticField geoField;

	private static final float ALPHA = 0.15f;

	private Location portoLocation = new Location("FOR_TESTS");
	private boolean hasDirections = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_list);
		Globals.destinations.add(new Destination("Coimbra",
				new DestinationDetails("Aveiro", "Coimbra")));
		Globals.destinations.add(new Destination("Porto",
				new DestinationDetails("Aveiro", "Porto")));
		Globals.destinations.add(new Destination("Lisboa",
				new DestinationDetails("Aveiro", "Lisboa")));
		tvLocation = (TextView) findViewById(R.id.textView1);
		tvAddress = (TextView) findViewById(R.id.textView2);

		exList = (ExpandableListView) findViewById(R.id.expandableListView1);

		exAdpt = new DestinationListAdapter(this, Globals.destinations);
		exList.setIndicatorBounds(0, 20);

		portoLocation.setLatitude(41.158567);
		portoLocation.setLongitude(-8.628167299999999);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		mLocationHelper = new LocationHelper(this, new onLocationUpdate() {

			@Override
			public void locationUpdated(Location newLocation) {
				if (newLocation != null) {

					Globals.currentLocation = newLocation;
					geoField = new GeomagneticField(
							Double.valueOf(
									Globals.currentLocation.getLatitude())
									.floatValue(), Double.valueOf(
									Globals.currentLocation.getLongitude())
									.floatValue(), Double.valueOf(
									Globals.currentLocation.getAltitude())
									.floatValue(), System.currentTimeMillis());
					destBearing = Globals.currentLocation
							.bearingTo(portoLocation);

					tvLocation
							.setText(locationStringFromLocation(Globals.currentLocation));
					
				//	ivCompass =
					
					View tempView = exAdpt.getGroupView(2, false, null, exList);
					
					//ivCompass = (ImageView) tempView.findViewById(R.id.ivCompass);

					(new GetAddressTask(DestinationListActivity.this,
							new onFinish() {

								@Override
								public void finish(String address) {
									if (address != null) {
										Globals.currentAddress = address;

										// only for testing
										if (!hasDirections) {
											tvAddress.setText(address);

											/*
											 * final ProgressDialog dialog = new
											 * ProgressDialog(
											 * getApplicationContext());
											 * 
											 * dialog.setMessage(
											 * "Fetching directions...");
											 * dialog.show();
											 */

											FetchDirectionsTask fd_task = new FetchDirectionsTask(
													locationStringFromLocation(Globals.currentLocation),
													locationStringFromLocation(portoLocation),
													Utils.MODE_DRIVING,
													new OnFinish() {

														@Override
														public void finishOk(
																Directions result) {
															/*
															 * Toast.makeText(
															 * getApplicationContext
															 * (),
															 * "Download complete!"
															 * ,
															 * Toast.LENGTH_LONG
															 * ) .show();
															 */
															if (result.status
																	.equals("OK")) {
																tvAddress
																		.setText(result.routes
																				.get(0).legs
																				.get(0).duration.text);
																hasDirections = true;
															} else {
																tvAddress
																		.setText(result.status);
																hasDirections = true;
															}// dialog.dismiss();

														}

														@Override
														public void finishError(
																String status) {
															tvAddress
																	.setText(status);
															/*
															 * Toast.makeText(
															 * getApplicationContext
															 * (), "ERROR",
															 * Toast
															 * .LENGTH_LONG)
															 * .show();
															 */
															// dialog.dismiss();

														}

													});

											fd_task.execute();
										}
									}
								}
							})).execute(newLocation);
				}

			}

		});

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.attachToListView(exList);

		fab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						AddEditActivity.class);
				startActivity(intent);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		exList.setAdapter(exAdpt);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mMagnetometer,
				SensorManager.SENSOR_DELAY_UI);

		// TODO save and retrive location preferences
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (mLocationHelper != null && !mLocationHelper.isConnected()) {
			mLocationHelper.connect();
		}
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		if (mLocationHelper != null && mLocationHelper.isConnected()) {
			mLocationHelper.removeLocationUpdates();
			mLocationHelper.disconnect();
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mMagnetometer);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer) {

			accelVals = lowPass(event.values.clone(), accelVals);
		} else if (event.sensor == mMagnetometer) {
			compassVals = lowPass(event.values.clone(), compassVals);
		}
		if (accelVals != null && compassVals != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, accelVals,
					compassVals);
			if (success) {
				SensorManager.getOrientation(R, mOrientation);

				float azimuthInRadians = mOrientation[0];

				float azimuthInDegress = (float) Math.round((Math
						.toDegrees(azimuthInRadians) + 360) % 360);

				float azimuth = azimuthInDegress;
				float baseAzimuth = azimuth;

				// converts magnetic north into true north
				if (geoField != null) {
					azimuth -= geoField.getDeclination();
				}

				// If the bearTo is smaller than 0, add 360 to get the rotation
				// clockwise.
				destBearing = (destBearing + 360) % 360;

				heading = (float) Math
						.round((destBearing - azimuth + 360) % 360);

				String bearingText = "N";

				if ((360 >= baseAzimuth && baseAzimuth >= 337.5)
						|| (0 <= baseAzimuth && baseAzimuth <= 22.5))
					bearingText = "N";
				else if (baseAzimuth > 22.5 && baseAzimuth < 67.5)
					bearingText = "NE";
				else if (baseAzimuth >= 67.5 && baseAzimuth <= 112.5)
					bearingText = "E";
				else if (baseAzimuth > 112.5 && baseAzimuth < 157.5)
					bearingText = "SE";
				else if (baseAzimuth >= 157.5 && baseAzimuth <= 202.5)
					bearingText = "S";
				else if (baseAzimuth > 202.5 && baseAzimuth < 247.5)
					bearingText = "SW";
				else if (baseAzimuth >= 247.5 && baseAzimuth <= 292.5)
					bearingText = "W";
				else if (baseAzimuth > 292.5 && baseAzimuth < 337.5)
					bearingText = "NW";
				else
					bearingText = "?";

				tvAddress.setText("Heading: " + Float.toString(heading)
						+ " degrees");
				
				Globals.tvDesc.setText(bearingText);

				RotateAnimation ra = new RotateAnimation(mCurrentDegree,
						-heading, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);

				ra.setDuration(250);

				ra.setFillAfter(true);

				Globals.ivCompass.startAnimation(ra);
				mCurrentDegree = -heading;
			}
		}
	}

	public static String locationStringFromLocation(final Location location) {
		return Location
				.convert(location.getLatitude(), Location.FORMAT_DEGREES)
				+ ","
				+ Location.convert(location.getLongitude(),
						Location.FORMAT_DEGREES);
	}

	protected float[] lowPass(float[] newVals, float[] oldVals) {
		if (oldVals == null)
			return newVals;

		for (int i = 0; i < newVals.length; i++) {
			oldVals[i] = oldVals[i] + ALPHA * (newVals[i] - oldVals[i]);
		}
		return oldVals;
	}

}
