package com.impecabel.betherein;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Utils {
	public static final String PLACES_DETAIL_API = "https://maps.googleapis.com/maps/api/place/details/json";
	public static final String PLACES_AUTOCOMPLETE_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	public static final String GMAPS_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json";
	
	public static final String PLACES_API_KEY = "AIzaSyCspnej6Jt999lE8kFmSs0_id9vyF26kvs";
	
	public static final HttpTransport HTTP_TRANSPORT = AndroidHttp
			.newCompatibleTransport();
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static final String MODE_DRIVING = "driving";
    public static final String MODE_WALKING = "walking";
    public static final String MODE_BICYCLING = "bicycling";
}
