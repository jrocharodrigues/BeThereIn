package com.impecabel.betherein;

import com.google.android.gms.maps.model.LatLng;

public class Place {
	private String place_id;
	private String description;
	private String formatted_address;
	private LatLng location;
	private boolean use_current_location;
	
	public Place(String place_id, String description) {
		this.place_id = place_id;
		this.description = description;
		this.use_current_location = false;
	}
	
	public Place(boolean use_current_location ) {
		this.place_id = null;
		this.description = null;
		this.use_current_location = use_current_location;
	}	

	/**
	 * @return the location
	 */
	public LatLng getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(LatLng location) {
		this.location = location;
	}

	/**
	 * @return the formated_address
	 */
	public String getFormatted_address() {
		return formatted_address;
	}

	/**
	 * @param formated_address the formated_address to set
	 */
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	/**
	 * @return the place_id
	 */
	public String getPlace_id() {
		return place_id;
	}

	/**
	 * @param place_id the place_id to set
	 */
	public void setPlace_id(String place_id) {
		this.place_id = place_id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the use_current_location
	 */
	public boolean isUse_current_location() {
		return use_current_location;
	}

	/**
	 * @param use_current_location the use_current_location to set
	 */
	public void setUse_current_location(boolean use_current_location) {
		this.use_current_location = use_current_location;
	}

	@Override
	public String toString() {
		return this.description;
	}
	
}
