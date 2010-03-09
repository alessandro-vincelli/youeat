package it.av.youeat.web.page.xml;

import it.av.youeat.web.xml.AtomGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.io.FeedException;

/**
 * @author Alessandro Vincelli
 */
public class FeedPage extends WebPage {

	@SpringBean
	private AtomGenerator atomGenerator;
	private static Log log = LogFactory.getLog(FeedPage.class);

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public FeedPage() throws IOException {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			outputter.output(atomGenerator.generate(), stream);
			Label sitemap = new Label("sitemap", new String(stream.toByteArray(), "UTF-8"));
			sitemap.setRenderBodyOnly(true);
			sitemap.setEscapeModelStrings(false);
			add(sitemap);
		} catch (IOException e) {
			log.error(e);
		} catch (FeedException e) {
			log.error(e);
		} finally {
			stream.close();
		}

	}

	@Override
	public String getMarkupType() {
		return "xml";
	}

}
