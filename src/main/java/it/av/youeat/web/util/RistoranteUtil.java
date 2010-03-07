/**
 *
 */
package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.YoueatHttpParams;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WicketURLEncoder;
import org.apache.wicket.request.RequestParameters;

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
        parameters.add(YoueatHttpParams.RISTORANTE_NAME_AND_CITY, cleansNameAndCity(risto));
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
        map.put(YoueatHttpParams.RISTORANTE_NAME_AND_CITY, cleansNameAndCity(risto));
        map.put(YoueatHttpParams.RISTORANTE_ID, risto.getId());
        parameters.setParameters(map);
        parameters.setPath("www.youeat.org");
        return parameters;
    }

    /**
     * Replaces empty space with underscore, and encodes the URL
     *
     * @param risto
     * @return cleaned name + city
     */
    private static String cleansNameAndCity(Ristorante risto) {
        String cleanedNameandCity = StringUtils.replace(risto.getName() + " " + risto.getCity().getName(), " ", "_");
        cleanedNameandCity = WicketURLEncoder.QUERY_INSTANCE.encode(cleanedNameandCity);
        return cleanedNameandCity;
    }

}
