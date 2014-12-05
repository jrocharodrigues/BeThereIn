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
import android.widget.Switch;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;
import com.impecabel.betherein.FetchPlaceDetailsTask.OnFinish;

public class AddEditActivity extends ActionBarActivity implements OnItemClickListener {

	private Place dest;
//	private Place orig;

	

	

	protected static final int RESULT_CODE = 123;
	private AutoCompleteTextView atvFrom;
	private AutoCompleteTextView atvTo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		atvFrom = (AutoCompleteTextView) findViewById(R.id.from);
		atvTo = (AutoCompleteTextView) findViewById(R.id.to);
		

		atvFrom.setAdapter(new PlacesAutoCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line));
		atvTo.setAdapter(new PlacesAutoCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line));

		atvTo.setOnItemClickListener(this);
		atvFrom.setOnItemClickListener(this);

		//orig = new Place(true);
		dest = new Place(false);

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
					} else {
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
							request.setParser(new JsonObjectParser(Utils.JSON_FACTORY));
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
				resultList
						.add(new Place(prediction.place_id, prediction.description));
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
			/*Destination new_dest = new Destination(etDescription.getText()
					.toString(), new DestinationDetails("lalal",
					dest.getDescription()));
			new_dest.setDestination(dest);
			new_dest.setOrigin(orig);
			Globals.destinations.add(new_dest);
			this.finish();
			return true;*/
			
			final ProgressDialog dialog = new ProgressDialog(this);
			
			dialog.setMessage("Saving...");
			dialog.show();
			
			FetchPlaceDetailsTask pd_fetcher = new FetchPlaceDetailsTask(dest.getPlace_id(), new OnFinish() {

				@Override
				public void finishOk(Place result) {
					Toast.makeText(getApplicationContext(), "Download complete!",
							Toast.LENGTH_LONG).show();
					Toast.makeText(getApplicationContext(), result.getFormatted_address(),
							Toast.LENGTH_LONG).show();
					dialog.dismiss();
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

	public void onSwitchClicked(View view) {
		// Is the toggle on?
		boolean on = ((Switch) view).isChecked();
		AutoCompleteTextView tvOrigin = (AutoCompleteTextView) findViewById(R.id.from);

		if (on) {
			tvOrigin.setVisibility(View.GONE);
		} else {
			tvOrigin.setVisibility(View.VISIBLE);
		}
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Place selected_place = (Place) adapterView.getItemAtPosition(position);
		Toast.makeText(this, selected_place.getPlace_id() + "", Toast.LENGTH_SHORT)
				.show();

		dest.setDescription(selected_place.getDescription());
		dest.setPlace_id(selected_place.getPlace_id());
		// adapterView.getItemAtPosition(position).
	}

}
