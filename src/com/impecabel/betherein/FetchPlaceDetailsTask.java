package com.impecabel.betherein;

import android.location.Location;
import android.os.AsyncTask;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.Key;

public class FetchPlaceDetailsTask extends AsyncTask<Void, Void, Place> {

	public interface OnFinish {
		public void finishOk(Place result);

		public void finishError();
	}

	private Place place;

	private OnFinish callback;
	private String place_id;

	public FetchPlaceDetailsTask(String place_id, OnFinish callback) {
		this.place_id = place_id;
		this.callback = callback;
	}

	@Override
	protected Place doInBackground(Void... params) {

		place = new Place(false);
		try {

			HttpRequestFactory requestFactory = Utils.HTTP_TRANSPORT
					.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) {
							request.setParser(new JsonObjectParser(
									Utils.JSON_FACTORY));
						}
					});

			GenericUrl url = new GenericUrl(Utils.PLACES_DETAIL_API);

			url.put("key", Utils.PLACES_API_KEY);
			url.put("placeid", place_id);

			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			PlaceDetailsResult placedetailsresult = httpResponse
					.parseAs(PlaceDetailsResult.class);
			PlaceDetails placedetails = placedetailsresult.placedetails;
			place.setPlace_id(place_id);
			place.setFormatted_address(placedetails.formatted_address);
			Location placeLocation = new Location("PlaceDetailsApi");
			placeLocation.setLatitude(placedetails.geometry.location.lat);
			placeLocation.setLongitude(placedetails.geometry.location.lng);
			place.setLocation(placeLocation);

			return place;

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return null;

	}

	@Override
	protected void onPostExecute(Place result) {
		if (callback == null) {
			return;
		}

		if (result == null) {
			callback.finishError();
		} else {
			callback.finishOk(result);
		}
	}

	public static class PlaceDetailsResult {

		@Key("result")
		public PlaceDetails placedetails;

	}

	public static class PlaceDetails {

		@Key("formatted_address")
		public String formatted_address;

		@Key("geometry")
		public PlaceGeometry geometry;

	}

	public static class PlaceGeometry {

		@Key("location")
		public PlaceLocation location;

	}

	public static class PlaceLocation {

		@Key("lat")
		public double lat;

		@Key("lng")
		public double lng;

	}

}
