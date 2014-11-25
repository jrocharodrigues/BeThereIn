package com.impecabel.betherein;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;

public class FetchDirectionsTask extends AsyncTask<Void, Void, Directions> {

	public final static String STATUS_OK = "OK";
    public final static String STATUS_NOT_FOUND = "NOT_FOUND";
    public final static String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public final static String STATUS_MAX_WAYPOINTS_EXCEEDED = "MAX_WAYPOINTS_EXCEEDED";
    public final static String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
    public final static String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public final static String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public final static String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";
	
	public interface OnFinish {
		public void finishOk(Directions result);

		public void finishError(String Status);
	}

	private Directions directions;

	private OnFinish callback;
	private String origin;
	private String dest;
	private String mode;
	
	private GenericUrl url;
	


	public FetchDirectionsTask(String origin, String dest, String mode, OnFinish callback) {
		this.origin = origin;
		this.dest = dest;
		this.mode = mode;
		this.callback = callback;
	}

	@Override
	protected Directions doInBackground(Void... params) {

		directions = new Directions();
		Long tsLong = System.currentTimeMillis() / 1000L;
		String ts = tsLong.toString();
		try {

			HttpRequestFactory requestFactory = Utils.HTTP_TRANSPORT
					.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) {
							request.setParser(new JsonObjectParser(
									Utils.JSON_FACTORY));
						}
					});

			url = new GenericUrl(Utils.GMAPS_DIRECTIONS_API);

			url.put("key", Utils.PLACES_API_KEY);
			url.put("origin", origin);
			url.put("destination", dest);
			url.put("mode", mode);
						
			url.put("units", "metric");
			url.put("departure_time", ts);

			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			directions = httpResponse
					.parseAs(Directions.class);
			
			return directions;
			

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return null;

	}

	@Override
	protected void onPostExecute(Directions result) {
		if (callback == null) {
			return;
		}
		
		Log.d("GetAddressTask", result.status);
		Log.d("GetAddressTask", "URL:" + url.toString() );

		if (result == null || !result.status.equals(STATUS_OK)) {
			callback.finishError(result.status);
		} else {
			
			callback.finishOk(result);
		}
	}

	
}
