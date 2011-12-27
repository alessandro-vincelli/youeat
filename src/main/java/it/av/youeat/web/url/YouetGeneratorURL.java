package it.av.youeat.web.url;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.util.EaterUtil;
import it.av.youeat.web.util.RistoranteUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Utility method to generate YouEat URL for specific entities
 * 
 * @author Alessandro Vincelli
 */
public class YouetGeneratorURL {
    private String baseURL;

    /**
     * generate the URL for the given risto
     * 
     * @param risto
     * @return url for the risto
     */
    public String getRistoranteUrl(Ristorante risto, WebPage page) {
//        MixedParamUrlCodingStrategy mixedParamUrlCodingStrategy = new MixedParamUrlCodingStrategy(
//                YouEatPagePaths.VIEW_RISTORANTE, RistoranteViewPage.class,
//                new String[] { YoueatHttpParams.RISTORANTE_NAME_AND_CITY });
        PageParameters parameters = RistoranteUtil.createParamsForRisto(risto);
        return baseURL + RequestCycle.get().getUrlRenderer().renderRelativeUrl(Url.parse(page.urlFor(RistoranteViewPage.class,parameters).toString()));
        
//        IRequestTarget target = new MixedParamHybridUrlCodingStrategy.HybridBookmarkablePageRequestTarget(null,
//                RistoranteViewPage.class, parameters, 0, false);
//        return baseURL + mixedParamUrlCodingStrategy.encode(target).toString();
    }

    /**
     * generate the URL for the given eater
     * 
     * @param eater
     * @return url for the eater
     */
    public String getEaterUrl(Eater eater, WebPage page) {
        //MixedParamHybridUrlCodingStrategy mixedParamUrlCodingStrategy = new MixedParamHybridUrlCodingStrategy(
                //YouEatPagePaths.VIEW_EATER, EaterViewPage.class, new String[] { YoueatHttpParams.YOUEAT_ID });
        PageParameters parameters = EaterUtil.createParamsForEater(eater);
//        IRequestTarget target = new MixedParamHybridUrlCodingStrategy.HybridBookmarkablePageRequestTarget(null,
//                EaterViewPage.class, parameters, 0, false);
//        return baseURL + mixedParamUrlCodingStrategy.encode(target).toString();
        return baseURL + RequestCycle.get().getUrlRenderer().renderRelativeUrl(Url.parse(page.urlFor(EaterViewPage.class,parameters).toString()));
    }

    /**
     * Base path for the application, usually: http://www.youeat.org
     * 
     * @param baseURL
     */
    public void setBaseURL(String baseURL) {
        if (!StringUtils.endsWith(baseURL, "/")) {
            this.baseURL = baseURL + "/";
        } else {
            this.baseURL = baseURL;
        }
    }
}