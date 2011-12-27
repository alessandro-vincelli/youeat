package it.av.youeat.batch.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class RistoranteBatchUtilTest {

    private static final Map<String, String> addressToTest;
    static {
        Map<String, String> listRemove = new HashMap<String, String>();
//        listRemove.put("1, Via Verdi Giuseppe", "Via Verdi Giuseppe, 1");
//        listRemove.put("556, Via Fiorenzuola", "Via Fiorenzuola, 556");
//        listRemove.put("57/a, Via Ragone", "Via Ragone, 57/a");
        listRemove.put("FRAZIONE POGGIO", "Frazione Poggio");
        addressToTest = Collections.unmodifiableMap(listRemove);
    }

    @Test
    @Ignore
    public void testCreateName() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateAddress() {
        for (String address : addressToTest.keySet()) {
            assertEquals(addressToTest.get(address), RistoranteBatchUtil.createAddress(address));
        }
    }

}
