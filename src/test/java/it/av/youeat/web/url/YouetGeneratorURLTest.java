package it.av.youeat.web.url;

import static org.junit.Assert.assertEquals;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.web.page.YoueatHttpParams;
import it.av.youeat.web.util.RistoranteUtil;

import org.junit.Ignore;
import org.junit.Test;

public class YouetGeneratorURLTest {

    @Test
    @Ignore("now getRistoranteUrl need a page")
    public void getEaterUrlTest() {
        Eater eater = new Eater("pwd", "Dante", "Cruciani", "email");
        eater.setId("1234");
        YouetGeneratorURL generatorURL = new YouetGeneratorURL();
        generatorURL.setBaseURL("http://www.youeat.org");
        String result = generatorURL.getEaterUrl(eater, null);
        assertEquals("http://www.youeat.org" + YouEatPagePaths.VIEW_EATER + "/1234", result);
    }

    @Test
    @Ignore("now getRistoranteUrl need a page")
    public void getRistoranteUrlTest() {
        Ristorante risto = new Ristorante();
        risto.setName("risto name");
        risto.setCity(new City("city name"));
        risto.setId("1234");
        YouetGeneratorURL generatorURL = new YouetGeneratorURL();
        generatorURL.setBaseURL("http://www.youeat.org");
        String result = generatorURL.getRistoranteUrl(risto, null);
        assertEquals("http://www.youeat.org" + YouEatPagePaths.VIEW_RISTORANTE + "/" + RistoranteUtil.cleansNameAndCity(risto)
                + "?" + YoueatHttpParams.RISTORANTE_ID + "=1234", result);
    }

}
