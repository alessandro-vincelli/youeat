package it.av.youeat.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public final class PeriodUtil {

    @Autowired
    private MessageSource ms;
    
    private static final String ND = "not available";

    /**
     * Return the period passed between the actual time and the given time
     * <p>The period is approximated to the biggest field:
     * Example if the period is 2 days and 3 hours the results is: 2 days 
     * 
     * @param time
     * @return the period
     */
    public String getPeriod(long time, Locale locale){
        Period period = new Period(time, System.currentTimeMillis());
        return StringUtils.substringBefore(standard(locale).print(period), "$");
    }
    
    /**
     * Example:
     * 1 month-4 weeks-2 days-23 hours-59 minutes-59 seconds
     * Milliseconds are skipped.
     * 
     * @param locale
     * @return the formatter
     */
    private PeriodFormatter standard(Locale locale) {
        PeriodFormatter cStandard = new PeriodFormatterBuilder()
            .appendYears()
            .appendSuffix(ms.getMessage("pt.year", null, locale), ms.getMessage("pt.years", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendMonths()
            .appendSuffix(ms.getMessage("pt.month", null, locale), ms.getMessage("pt.months", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendWeeks()
            .appendSuffix(ms.getMessage("pt.week", null, locale), ms.getMessage("pt.weeks", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendDays()
             .appendSuffix(ms.getMessage("pt.day", null, locale), ms.getMessage("pt.days", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendHours()
            .appendSuffix(ms.getMessage("pt.hour", null, locale), ms.getMessage("pt.hours", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendMinutes()
            .appendSuffix(ms.getMessage("pt.minute", null, locale), ms.getMessage("pt.minutes", null, ND, locale))
            .appendSeparatorIfFieldsBefore("$")
            .appendSeconds()
            .appendSuffix(ms.getMessage("pt.second", null, locale), ms.getMessage("pt.seconds", null, ND, locale))
            .toFormatter();
        return cStandard;
    }
}
