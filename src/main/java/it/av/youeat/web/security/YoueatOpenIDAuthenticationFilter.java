package it.av.youeat.web.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.openid.OpenIDAuthenticationFilter;

/**
 * 
 * 
 * @author alessandro vincelli
 *
 */
public class YoueatOpenIDAuthenticationFilter extends OpenIDAuthenticationFilter {

    private String returnTO;

    @Override
    protected String buildReturnToUrl(HttpServletRequest request) {
        if (StringUtils.isNotBlank(returnTO)) {
            return returnTO;
        } else {
            return super.buildReturnToUrl(request);
        }
    }

    /**
     * Sets the <tt>return_to</tt> URL that will be sent to the OpenID service provider<br/>
     * 
     * example: "http://www.youeat.org"
     * 
     * @param returnTO
     */
    public void setReturnTO(String returnTO) {
        this.returnTO = returnTO;
    }

}
