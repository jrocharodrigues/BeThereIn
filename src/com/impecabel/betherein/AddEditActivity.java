package com.impecabel.betherein;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;
import com.impecabel.betherein.FetchPlaceDetailsTask.OnFinish;

public class AddEditActivity extends ActionBarActivity implements
		OnItemClickListener {

	private Place destPlace;

	protected static final int RESULT_CODE = 123;
	private TextView etDescription;
	private AutoCompleteTextView atvTo;
	private View viewDescription;
	private int destPosition;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		destPosition = getIntent().getIntExtra("position", -1);

		etDescription = (TextView) findViewById(R.id.etDescription);
		atvTo = (AutoCompleteTextView) findViewById(R.id.to);	
		
		atvTo.setAdapter(new PlacesAutoCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line));

		atvTo.setOnItemClickListener(this);
		
		viewDescription = findViewById(R.id.LayoutDescription);

		// orig = new Place(true);
		destPlace = new Place(false);
		int pos;
		
	}


	private class PlacesAutoCompleteAdapter extends ArrayAdapter<Place>
			implements Filterable {
		private ArrayList<Place> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public Place getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
						getSupportActionBar().hide();
						viewDescription.setVisibility(View.GONE);
					} else {
						getSupportActionBar().show();
						viewDescription.setVisibility(View.VISIBLE);
						
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	private ArrayList<Place> autocomplete(String input) {

		ArrayList<Place> resultList = new ArrayList<Place>();

		try {

			HttpRequestFactory requestFactory = Utils.HTTP_TRANSPORT
					.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) {
							request.setParser(new JsonObjectParser(
									Utils.JSON_FACTORY));
						}
					});

			GenericUrl url = new GenericUrl(Utils.PLACES_AUTOCOMPLETE_API);
			url.put("input", input);
			url.put("key", Utils.PLACES_API_KEY);
			url.put("sensor", false);

			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			PlacesResult directionsResult = httpResponse
					.parseAs(PlacesResult.class);

			List<Prediction> predictions = directionsResult.predictions;
			for (Prediction prediction : predictions) {
				resultList.add(new Place(prediction.place_id,
						prediction.description));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultList;
	}

	public static class PlacesResult {

		@Key("predictions")
		public List<Prediction> predictions;

	}

	public static class Prediction {
		@Key("description")
		public String description;

		@Key("id")
		public String id;

		@Key("place_id")
		public String place_id;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_accept:

			final ProgressDialog dialog = new ProgressDialog(this);

			dialog.setMessage("Saving...");
			dialog.show();

			FetchPlaceDetailsTask pd_fetcher = new FetchPlaceDetailsTask(
					destPlace.getPlace_id(), new OnFinish() {

						@Override
						public void finishOk(Place result) {
							Toast.makeText(getApplicationContext(),
									"Download complete!", Toast.LENGTH_LONG)
									.show();
							Toast.makeText(getApplicationContext(),
									result.getFormatted_address(),
									Toast.LENGTH_LONG).show();
							
							destPlace.setFormatted_address(result.getFormatted_address());
							destPlace.setLocation(result.getLocation());							
						
							dialog.dismiss();
							saveDestination(destPosition);
							finish();
						}

						@Override
						public void finishError() {
							Toast.makeText(getApplicationContext(), "ERROR",
									Toast.LENGTH_LONG).show();
							dialog.dismiss();

						}
					});
			pd_fetcher.execute();

		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		getSupportActionBar().show();
		viewDescription.setVisibility(View.VISIBLE);
		destPlace = (Place) adapterView.getItemAtPosition(position);
		Toast.makeText(this, destPlace.getPlace_id() + "",
				Toast.LENGTH_SHORT).show();
	}
	
	private void saveDestination(int pos){
		Destination newDestination = new Destination(etDescription.getText().toString(), destPlace, new Place(true));
		Globals.destinations.add(newDestination);
		
	}

}
