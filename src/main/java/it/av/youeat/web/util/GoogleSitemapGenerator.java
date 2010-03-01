package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.page.YoueatHttpParams;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Alessandro Vincelli
 */
public class GoogleSitemapGenerator {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String URLSET_START = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
    private static final String URLSET_END = "</urlset>";
    private static final String baseURL = "http://www.youeat.org/";

    @Autowired
    private RistoranteService ristoranteService;

    public String run() {
        MixedParamUrlCodingStrategy mixedParamUrlCodingStrategy = new MixedParamUrlCodingStrategy("/viewRistorante", RistoranteViewPage.class, new String[]{YoueatHttpParams.RISTORANTE_NAME_AND_CITY});
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
            IRequestTarget target = mixedParamUrlCodingStrategy.decode(RistoranteUtil.createRequestParamsForRisto(ristorante));
            String url =  baseURL + mixedParamUrlCodingStrategy.encode(target).toString();
            sb.append(generateUrl(url, ristorante.getModificationTime(), "weekly", "0.2"));
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
     * @param frequency the frequency: daily, montly, weekly
     * @param prio priority: 1.0 - 0.8 - 0.2
     *
     */
    private StringBuffer generateUrl(String loc, Date lastMod, String frequency, String prio) {
        StringBuffer sb = new StringBuffer();
        sb.append("<url>");
        sb.append("<loc>");
        sb.append(loc);
        sb.append("</loc>");
        sb.append("<lastmod>");
        sb.append(DateUtil.SDF2SIMPLEUSA.print(lastMod.getTime()));
        sb.append("</lastmod>");
        sb.append("<changefreq>");
        sb.append(frequency);
        sb.append("</changefreq>");
        sb.append("<priority>");
        sb.append(prio);
        sb.append("</priority>");
        sb.append("</url>");

        return sb;
    }

}
