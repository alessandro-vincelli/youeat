package it.av.youeat.web.page;

import it.av.youeat.web.util.GoogleSitemapGenerator;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Alessandro Vincelli
 */
public class Sitemap extends WebPage {

    @SpringBean
    private GoogleSitemapGenerator sitemapGenerator;

    public Sitemap() {
        Label sitemap = new Label("sitemap", new Model(sitemapGenerator.run()));
        sitemap.setRenderBodyOnly(true);
        sitemap.setEscapeModelStrings(false);
        add(sitemap);
    }

    @Override
    public String getMarkupType() {
        return "xml";
    }

}
