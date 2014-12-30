package com.impecabel.betherein;

import garin.artemiy.compassview.library.CompassSensorsActivity;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.impecabel.betherein.FetchDirectionsTask.OnFinish;
import com.impecabel.betherein.GetAddressTask.onFinish;
import com.impecabel.betherein.LocationHelper.onLocationUpdate;
import com.melnykov.fab.FloatingActionButton;

public class DestinationListActivity extends CompassSensorsActivity implements
		SensorEventListener {
	DestinationListAdapter destinatonAdapter;
	RecyclerView recyclerView;

	private LocationHelper mLocationHelper;
	private TextView tvLocation;
	private TextView tvAddress;

	// private Location portoLocation = new Location("FOR_TESTS");
	private boolean hasDirections = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_list);

		add_dummy_data();
		tvLocation = (TextView) findViewById(R.id.textView1);
		tvAddress = (TextView) findViewById(R.id.textView2);

		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

		destinatonAdapter = new DestinationListAdapter(this,
				Globals.destinations);

		recyclerView.setAdapter(destinatonAdapter);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		mLocationHelper = new LocationHelper(this, new onLocationUpdate() {

			@Override
			public void locationUpdated(Location newLocation) {
				if (newLocation != null) {

					tvLocation
							.setText(locationStringFromLocation(Globals.currentLocation));

					(new GetAddressTask(DestinationListActivity.this,
							new onFinish() {

								@Override
								public void finish(String address) {
									if (address != null) {
										Globals.currentAddress = address;

										// only for testing
										if (!hasDirections) {
											tvAddress.setText(address);

											FetchDirectionsTask fd_task = new FetchDirectionsTask(
													locationStringFromLocation(Globals.currentLocation),
													locationStringFromLocation(Globals.portoLocation),
													Utils.MODE_DRIVING,
													new OnFinish() {

														@Override
														public void finishOk(
																Directions result) {

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
															}

														}

														@Override
														public void finishError(
																String status) {
															tvAddress
																	.setText(status);

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
		fab.attachToRecyclerView(recyclerView);

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		recyclerView.setAdapter(destinatonAdapter);

	}

	public static String locationStringFromLocation(final Location location) {
		return Location
				.convert(location.getLatitude(), Location.FORMAT_DEGREES)
				+ ","
				+ Location.convert(location.getLongitude(),
						Location.FORMAT_DEGREES);
	}


	private void add_dummy_data() {
		Globals.portoLocation = new Location("FOR_TESTS");
		Globals.staticCurrentLocation = new Location("FOR_TESTS");
		Globals.portoLocation.setLatitude(40.642989811262765);
		Globals.portoLocation.setLongitude(-8.646524548530579);
		Globals.staticCurrentLocation.setLatitude(40.64186191152463);
		Globals.staticCurrentLocation.setLongitude(-8.643834292888641);
	}

}
