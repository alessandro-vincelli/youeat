package it.av.youeat.ocm.model.geo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Davide Cerbo
 *
 */
public class GeoCoordinate {
	
	public Double value;

	public GeoCoordinate(Double value) {
		this.value = value;
	}
	
	public GeoCoordinate radiant() {
		return new GeoCoordinate(2 * Math.PI * this.value / 360);
	}
	
	public Double sin(){
		return Math.sin(this.value);
	}
	
	public Double cos(){
		return Math.cos(this.value);
	}
	
	public Double acos(){
		return Math.acos(this.value);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
