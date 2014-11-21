package com.impecabel.betherein;

public class Destination {
	private String description;
	private Place destination;
	private Place origin;
	private DestinationDetails details;

	public Destination(String description, DestinationDetails details) {
		this.description = description;
		this.details = details;
	}

	/**
	 * @return the origin
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the details
	 */
	public DestinationDetails getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(DestinationDetails details) {
		this.details = details;
	}	

	/**
	 * @return the destination
	 */
	public Place getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Place destination) {
		this.destination = destination;
	}

	/**
	 * @return the origin
	 */
	public Place getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Place origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {

		return description;
	}

}

