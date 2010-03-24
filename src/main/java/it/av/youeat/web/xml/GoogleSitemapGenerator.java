package it.av.youeat.web.xml;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.url.YouetGeneratorURL;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alessandro Vincelli
 */
public class GoogleSitemapGenerator {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String URLSET_START = "<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
    private static final String URLSET_END = "</urlset>";
    private String baseURL;    

    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private YouetGeneratorURL ristoranteURL;

    /**
     * 
     * @return string containing google XML sitemap 
     */
    public String run() {
        StringBuffer sb = new StringBuffer();
        sb.append(XML_DECLARATION);
        sb.append("\n");
        sb.append(URLSET_START);
        sb.append("\n");
        //home page
        sb.append(generateUrl(baseURL, Calendar.getInstance().getTime(), "daily", "1.0"));
        sb.append("\n");
        Collection<Ristorante> ristoranteList = ristoranteService.getAll();
        for (Iterator<Ristorante> ristoranteIterator = ristoranteList.iterator(); ristoranteIterator.hasNext();) {
            Ristorante ristorante = ristoranteIterator.next();
            sb.append(generateUrl(ristoranteURL.getRistoranteUrl(ristorante), ristorante.getModificationTime(), "weekly", "0.2"));
            sb.append("\n");
        }
        sb.append(URLSET_END);
        return sb.toString();
    }


    /**
     * <loc>http://www.example.com/</loc>
     * <lastmod>2005-01-01</lastmod>
     * <changefreq>weekly</changefreq>
     * <priority>0.8</priority>
     *
     * @param loc the url;
     * @param lastMod last modification date
     * @param frequency the frequency: daily, monthly, weekly
     * @param prio priority: 1.0 - 0.8 - 0.2
     *
     */
    private StringBuffer generateUrl(String loc, Date lastMod, String frequency, String prio) {
        StringBuffer sb = new StringBuffer();
        sb.append("<url>\n");
        sb.append("<loc>");
        sb.append(loc);
        sb.append("</loc>\n");
        sb.append("<lastmod>");
        sb.append(ISODateTimeFormat.dateTime().print(lastMod.getTime()));
        sb.append("</lastmod>\n");
        sb.append("<changefreq>");
        sb.append(frequency);
        sb.append("</changefreq>\n");
        sb.append("<priority>");
        sb.append(prio);
        sb.append("</priority>\n");
        sb.append("</url>\n");
        return sb;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

}
