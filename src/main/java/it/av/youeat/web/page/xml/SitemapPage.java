package it.av.youeat.web.page.xml;

import it.av.youeat.web.xml.GoogleSitemapGenerator;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Alessandro Vincelli
 */
public class SitemapPage extends WebPage {

	@SpringBean
	private GoogleSitemapGenerator sitemapGenerator;

	/**
	 * Constructor
	 */
	public SitemapPage() {
		Label sitemap = new Label("sitemap", sitemapGenerator.run());
		sitemap.setRenderBodyOnly(true);
		sitemap.setEscapeModelStrings(false);
		add(sitemap);
	}

	@Override
	public String getMarkupType() {
		return "xml";
	}

}
