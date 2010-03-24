/**
 *
 */
package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.page.YoueatHttpParams;

import org.apache.wicket.PageParameters;

/**
 * Utils
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public final class EaterUtil {

    private EaterUtil() {
    }

    /**
     * Creates parameters for the given eater.
     * 
     * @param eater
     * @return parameters
     * @see YoueatHttpParams
     */
    public static PageParameters createParamsForEater(Eater eater) {
        PageParameters parameters = new PageParameters();
        parameters.add(YoueatHttpParams.YOUEAT_ID, eater.getId());
        return parameters;
    }
}
