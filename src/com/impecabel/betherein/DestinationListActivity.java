package com.impecabel.betherein;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.impecabel.betherein.GetAddressTask.onFinish;
import com.impecabel.betherein.LocationHelper.onLocationUpdate;
import com.melnykov.fab.FloatingActionButton;

public class DestinationListActivity extends Activity {
	DestinationListAdapter exAdpt;
	ExpandableListView exList;

	private LocationHelper mLocationHelper;
	private TextView tvLocation;
	private TextView tvAddress;
	private GetAddressTask get_address;

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

		mLocationHelper = new LocationHelper(this, new onLocationUpdate() {

			@Override
			public void locationUpdated(Location newLocation) {
				if (newLocation != null) {

					Globals.currentLocation = newLocation;
					tvLocation.setText(newLocation.getLatitude() + ","
							+ newLocation.getLongitude());
					(new GetAddressTask(DestinationListActivity.this,
							new onFinish() {

								@Override
								public void finish(String address) {
									if (address != null) {
										Globals.currentAddress = address;
										tvAddress.setText(address);
									}
								}
							})).execute(newLocation);
				}

			}

		});
		/*
		 * tvLocation.setText(mLocationHelper.getDeviceLocation().getLatitude()
		 * + "," + mLocationHelper.getDeviceLocation().getLongitude());
		 */

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
}
