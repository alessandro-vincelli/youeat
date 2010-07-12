package it.av.youeat.ocm.model.geo;

import static java.lang.Math.acos;
import static java.lang.Math.cos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;

/**
 * 
 * @author Davide Cerbo
 * @author Alessandro Vincelli
 * 
 */
public class Location {

    private GeoCoordinate latitude;
    private GeoCoordinate longitude;

    /**
     * default empty constructor
     */
    public Location() {
    }

    /**
     * Construct a position using latitude and longitude
     * 
     * @param latitude (not null)
     * @param longitude (not null)
     */
    public Location(Double latitude, Double longitude) {
        Assert.notNull(latitude, "latitude null");
        Assert.notNull(longitude, "longitude null");
        this.latitude = new GeoCoordinate(latitude);
        this.longitude = new GeoCoordinate(longitude);
    }

    /**
     * Construct a position using latitude and longitude
     * 
     * @param latitude
     * @param longitude
     */
    public Location(GeoCoordinate latitude, GeoCoordinate longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * return the distance in meters with the given location
     * 
     * @param location
     * @return the distance in meters
     */
    public Long distanceFrom(Location location) {
        return (long) (distanceFromInRadiant(location) * 6372795.477598);
    }

    /**
     * TODO To be verified the value, maybe not valid to use in distance calculation like in the SQL query, check the initial
     * acos()
     * 
     * return the distance in radiant with the given location
     * 
     * @param location
     * @return the distance in radiant
     */
    private Double distanceFromInRadiant(Location location) {
        GeoCoordinate thisLat = this.latitude.radiant();
        GeoCoordinate otherLat = location.latitude.radiant();
        GeoCoordinate thisLon = this.longitude.radiant();
        GeoCoordinate otherLon = location.longitude.radiant();
        return acos(thisLat.sin() * otherLat.sin() + thisLat.cos() * otherLat.cos() * cos(thisLon.value - otherLon.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * @return radiant values for the locations
     */
    public Location toRadiant() {
        return new Location(this.latitude.radiant(), this.longitude.radiant());
    }

    /**
     * @return the latitude
     */
    public GeoCoordinate getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(GeoCoordinate latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public GeoCoordinate getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(GeoCoordinate longitude) {
        this.longitude = longitude;
    }
    
}