package it.av.youeat.web.page.xml;

import it.av.youeat.web.xml.AtomGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Vincelli
 */
public class FeedPage extends WebPage {

    @SpringBean
    private AtomGenerator atomGenerator;
    private static Logger log = LoggerFactory.getLogger(FeedPage.class);

    /**
     * Constructor
     * 
     * @throws IOException
     */
    public FeedPage() throws IOException {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            outputter.output(atomGenerator.generate(this), stream);
            Label sitemap = new Label("sitemap", new String(stream.toByteArray(), "UTF-8"));
            sitemap.setRenderBodyOnly(true);
            sitemap.setEscapeModelStrings(false);
            add(sitemap);
        } catch (Exception e) {
            log.error("Error creating FEED Page", e);
        } finally {
            stream.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        response.setContentType("text/xml"); 
    }

}
