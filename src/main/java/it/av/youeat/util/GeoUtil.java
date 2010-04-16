/**
 * 
 */
package it.av.youeat.util;

import static java.lang.Math.acos;

/**
 * @author Alessandro Vincelli
 *
 */
public final class GeoUtil {

    public static final Double RADIUS = 6372795.477598;
    
    /**
     * apply the following: acos(valueToConvert) * 6372795.477598
     * 
     * @param valueToConvert
     * @return converted value
     */
    public static final Long toMeter(Double valueToConvert){
        return (long)(acos(valueToConvert) * 6372795.477598);
    }
}
