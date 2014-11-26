package com.impecabel.betherein;

import java.util.ArrayList;

import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;

public class Globals {
	public static ArrayList<Destination> destinations = new ArrayList<Destination>();
	public static Location currentLocation = new Location("FOR_TESTS");
	public static String currentAddress;
	public static Location portoLocation;
	public static Location staticCurrentLocation;
	public static ImageView ivCompass;
	public static TextView tvDesc;

}
