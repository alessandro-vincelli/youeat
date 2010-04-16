package it.av.youeat.ocm.model.geo;

import static java.lang.Math.acos;
import static java.lang.Math.cos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Davide Cerbo
 *
 */
public class Location {

	public GeoCoordinate latitude;
	public GeoCoordinate longitude;
	
	public Location() { }

	public Location(Double latitude, Double longitude) {
		this.latitude = new GeoCoordinate(latitude);
		this.longitude = new GeoCoordinate(longitude);
	}
	
	public Location(GeoCoordinate latitude, GeoCoordinate longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	public Double distanceFrom(Location location){
		GeoCoordinate thisLat = this.latitude.radiant();
		GeoCoordinate otherLat = location.latitude.radiant();
		GeoCoordinate thisLon = this.longitude.radiant();
		GeoCoordinate otherLon = location.longitude.radiant();
		return acos(thisLat.sin() * otherLat.sin() + thisLat.cos() * otherLat.cos() * cos(thisLon.value - otherLon.value)) * 6372795.477598;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public Location toRadiant() {
		return new Location(this.latitude.radiant(), this.longitude.radiant());
	}
	
}
