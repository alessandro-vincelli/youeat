package it.av.youeat.web.util;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.page.YoueatHttpParams;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;

/**
 * Utility method to generate YouEat URL for ristoranti
 * 
 * @author Alessandro Vincelli
 */
public final class GeneratorRistoranteURL {
	private String baseURL;

	/**
	 * default generator
	 */
	public GeneratorRistoranteURL() {
	}

	/**
	 * 
	 * @param risto
	 * @return url of the risto
	 */
	public String getUrl(Ristorante risto) {
		MixedParamUrlCodingStrategy mixedParamUrlCodingStrategy = new MixedParamUrlCodingStrategy("/viewRistorante",
		        RistoranteViewPage.class, new String[] { YoueatHttpParams.RISTORANTE_NAME_AND_CITY });
		IRequestTarget target = mixedParamUrlCodingStrategy.decode(RistoranteUtil.createRequestParamsForRisto(risto));
		String url = baseURL + mixedParamUrlCodingStrategy.encode(target).toString();
		return url;
	}

	/**
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
