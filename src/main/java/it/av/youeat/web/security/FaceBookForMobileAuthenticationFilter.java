package it.av.youeat.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * Filter to check and try authentication using FB
 * 
 * @author Alessandro Vincelli
 * 
 */
public class FaceBookForMobileAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * Constructor
     * 
     * @param defaultFilterProcessesUrl
     */
    protected FaceBookForMobileAuthenticationFilter() {
        super("/rest/security/signUpFB");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        FacebookAuthenticationToken authenticationToken = new FacebookAuthenticationToken(request);
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // boolean isRequried = super.requiresAuthentication(request, response);
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }
        return false;
    }
}
