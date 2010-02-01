/**
 * 
 */
package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.YoueatHttpParams;

import org.apache.wicket.PageParameters;

/**
 * Utils
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class RistoranteUtil {
    /**
     * Creates parameters for the given risto.
     * 
     * @see YoueatHttpParams
     * @param risto
     * @return parameters
     */
    public static PageParameters createParamsForRisto(Ristorante risto) {
        PageParameters parameters = new PageParameters();
        parameters.add(YoueatHttpParams.RISTORANTE_NAME_AND_CITY, risto.getName() + " " + risto.getCity().getName());
        parameters.add(YoueatHttpParams.RISTORANTE_ID, risto.getId());
        return parameters;
    }
}
