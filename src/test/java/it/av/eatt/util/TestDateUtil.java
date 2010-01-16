package it.av.eatt.util;

import static junit.framework.Assert.assertEquals;
import it.av.youeat.ocm.util.DateUtil;

import java.sql.Date;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class TestDateUtil {

    @Test
    public void testPrintPeriod() {
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.SECOND, -1);
        assertEquals(DateUtil.getPeriod(date1.getTimeInMillis()), "1 second");
        date1.setTime(new Date(System.currentTimeMillis()));
        date1.add(Calendar.HOUR, -2);
        assertEquals(DateUtil.getPeriod(date1.getTimeInMillis()), "2 hours");
        date1.setTime(new Date(System.currentTimeMillis()));
        date1.add(Calendar.MONTH, -2);
        assertEquals(DateUtil.getPeriod(date1.getTimeInMillis()), "2 months");
    }
}
