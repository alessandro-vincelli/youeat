package it.av.youeat.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.EaterService;
import it.av.youeat.web.url.YouetGeneratorURL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TemplateUtilTest {

    private YouetGeneratorURL mockGeneratorURL;
    private EaterService eaterService; 
    
    @Before
    public void setUp(){
        mockGeneratorURL = mock(YouetGeneratorURL.class);
        eaterService = mock(EaterService.class);
    }
    
    @Test
    @Ignore("page needed")
    public void resolveTemplateEaterTest() {
        Eater eater = new Eater("pwd", "Dante", "Cruciani", "email");
        eater.setId("1234");
        TemplateUtil util = new TemplateUtil(eaterService, mockGeneratorURL);
        when(eaterService.getByID("1234")).thenReturn(eater);

        String template = TemplateUtil.templateEater(eater);

        Message msg = new Message("", "some text " + template + " other text");
        String result = util.resolveTemplateEater(msg, false, null, null);
        assertEquals("some text Dante Cruciani other text", result);

        // test without template
        msg = new Message("", "some text other text");
        result = util.resolveTemplateEater(msg, false, null,  null);
        assertEquals("some text other text", result);

    }

    @Test
    @Ignore("page needed")
    public void extractNameAndUrlsTest() {

        // test without link
        Eater eater = new Eater("pwd", "Dante", "Cruciani", "email");
        eater.setId("1234");
        TemplateUtil util = new TemplateUtil(eaterService, mockGeneratorURL);
        String result = util.extractNameAndUrls(eater, false, null, null);
        assertEquals("Dante Cruciani", result);

        // test with link
        util = new TemplateUtil(eaterService, mockGeneratorURL);
        when(mockGeneratorURL.getEaterUrl(eater, null)).thenReturn("http://www.youeat.org/viewUser/id...");
        result = util.extractNameAndUrls(eater, true, null, null);
        assertEquals("<a href=\"http://www.youeat.org/viewUser/id...\">Dante Cruciani</a>", result);
        result = util.extractNameAndUrls(eater, true, "class", null);
        assertEquals("<a href=\"http://www.youeat.org/viewUser/id...\" class=\"class\">Dante Cruciani</a>", result);
    }

    @Test
    @Ignore("page needed")
    public void resolveTemplateEaterTest_extractingURLS() {
        Eater eater = new Eater("pwd", "Dante", "Cruciani", "email");
        eater.setId("1234");
        Eater eater2 = new Eater("pwd", "Marcello", "Mastroianni", "email2");
        eater2.setId("1235");
        Eater eater3 = new Eater("pwd", "Paolo", "Rossi", "email3");
        eater3.setId("1236");

        TemplateUtil util = new TemplateUtil(eaterService, mockGeneratorURL);
        when(eaterService.getByID("1234")).thenReturn(eater);
        when(eaterService.getByID("1235")).thenReturn(eater2);
        when(eaterService.getByID("1236")).thenReturn(eater3);

        String template = TemplateUtil.templateEater(eater);
        String template2 = TemplateUtil.templateEater(eater2);
        String template3 = TemplateUtil.templateEater(eater3);

        Message msg = new Message("", template + " some text " + template2 + " other text " + template3 + " closing.");
        String result = util.resolveTemplateEater(msg, false, null, null);
        assertEquals("Dante Cruciani some text Marcello Mastroianni other text Paolo Rossi closing.", result);

        // test extractin urls
        // youetGeneratorURL
        
        when(mockGeneratorURL.getEaterUrl(any(Eater.class), null)).thenReturn("http://link/");
        result = util.resolveTemplateEater(msg, true, null, null);
        assertEquals(
                "<a href=\"http://link/\">Dante Cruciani</a> some text <a href=\"http://link/\">Marcello Mastroianni</a> other text <a href=\"http://link/\">Paolo Rossi</a> closing.",
                result);

    }

}
