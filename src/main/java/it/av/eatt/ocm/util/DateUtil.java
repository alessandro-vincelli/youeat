/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.eatt.ocm.util;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateUtil {
    /**
     * Format used in the UI. dd/MM/yyyy HH:mm:ss
     */
    public static final DateTimeFormatter  sdf2Show =  DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    private static PeriodFormatter cStandard;

    /**
     * Return the actual time 
     * 
     * @return Timestamp
     */
    public static Timestamp getTimestamp(){
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }
    
    /**
     * Return the period passed between the actual time and the given time
     * <p>The period is approximated to the biggest field:
     * Example if the period is 2 days and 3 hours the results is: 2 days 
     * 
     * @param time
     * @return the period
     */
    public static String getPeriod(long time){
        Period period = new Period(time, System.currentTimeMillis());
        //get the substring before the first "$" separator
        return StringUtils.substringBefore(DateUtil.standard().print(period), "$");
    }
    
    /**
     * TODO  use Localization
     * Example:
     * 1 month-4 weeks-2 days-23 hours-59 minutes-59 seconds
     * Milliseconds are not output.
     * 
     * @return the formatter
     */
    private static PeriodFormatter standard() {
        if (cStandard == null) {
            cStandard = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparatorIfFieldsBefore("$")
                .appendMonths()
                .appendSuffix(" month", " months")
                .appendSeparatorIfFieldsBefore("$")
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .appendSeparatorIfFieldsBefore("$")
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparatorIfFieldsBefore("$")
                .appendHours()
                .appendSuffix(" hour", " hours")
                .appendSeparatorIfFieldsBefore("$")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparatorIfFieldsBefore("$")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();
        }
        return cStandard;
    }
}
