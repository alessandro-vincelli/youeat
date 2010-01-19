package it.av.eatt.util;

import static junit.framework.Assert.assertEquals;
import it.av.youeat.util.PeriodUtil;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class TestDateUtil {
    @Autowired
    private PeriodUtil periodUtil;

    @Test
    public void testPrintPeriod() {
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.SECOND, -1);
        assertEquals(periodUtil.getPeriod(date1.getTimeInMillis(), Locale.ENGLISH), "1 second ago");
        date1.setTime(new Date(System.currentTimeMillis()));
        date1.add(Calendar.HOUR, -2);
        assertEquals(periodUtil.getPeriod(date1.getTimeInMillis(), Locale.ENGLISH), "2 hours ago");
        date1.setTime(new Date(System.currentTimeMillis()));
        date1.add(Calendar.MONTH, -2);
        assertEquals(periodUtil.getPeriod(date1.getTimeInMillis(), Locale.ENGLISH), "2 months ago");
    }
}
