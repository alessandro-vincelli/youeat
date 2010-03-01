/**
 *
 */
package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.YoueatHttpParams;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;

import java.util.HashMap;

/**
 * Utils
 *
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public final class RistoranteUtil {

    private RistoranteUtil() {
    }

    ;

    /**
     * Creates parameters for the given risto.
     *
     * @param risto
     * @return parameters
     * @see YoueatHttpParams
     */
    public static PageParameters createParamsForRisto(Ristorante risto) {
        PageParameters parameters = new PageParameters();
        parameters.add(YoueatHttpParams.RISTORANTE_NAME_AND_CITY, risto.getName() + " " + risto.getCity().getName());
        parameters.add(YoueatHttpParams.RISTORANTE_ID, risto.getId());
        return parameters;
    }

    /**
     * Creates request parameters for the given risto.
     *
     * @param risto
     * @return parameters
     * @see YoueatHttpParams
     */
    public static RequestParameters createRequestParamsForRisto(Ristorante risto) {
        RequestParameters parameters = new RequestParameters();
        HashMap<String, String> map = new HashMap<String, String>();        
        map.put(YoueatHttpParams.RISTORANTE_NAME_AND_CITY, StringUtils.replace(risto.getName() + " " + risto.getCity().getName(), " ", "-"));
        map.put(YoueatHttpParams.RISTORANTE_ID, risto.getId());
        parameters.setParameters(map);
        parameters.setPath("www.youeat.org");
        return parameters;
    }

}
