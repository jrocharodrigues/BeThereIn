package com.impecabel.betherein;

import java.util.List;

import com.google.api.client.util.Key;

public class Directions {
	
	@Key("routes")
	public List<DirectionsRoutes> routes;
	
	@Key("status")
	public String status;
	
	@Key("error_message")
	public String error_message;
	
	
	public static class DirectionsRoutes {

		@Key("copyrights")
		public String copyrights;
		
		@Key("summary")
		public String summary;

		@Key("legs")
		public List<DirectionsLegs> legs;
		
		@Key("warnings")
		public List<String> warnings;

	}
	
	public static class DirectionsLegs {

		@Key("distance")
		public DirectionsDistance distance;

		@Key("duration")
		public DirectionsDuration duration;
		
		@Key("end_address")
		public String end_address;
		
		@Key("start_address")
		public String start_address;

	}
	
	public static class DirectionsDistance {

		@Key("text")
		public String text;

		@Key("value")
		public long value;

	}
	
	public static class DirectionsDuration {

		@Key("text")
		public String text;

		@Key("value")
		public long value;

	}

}
