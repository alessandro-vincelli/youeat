/**
 * 
 */
package it.av.youeat.web.security;

import it.av.youeat.ocm.model.Eater;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Simply the acces to to SpringSecurityContext
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class SecurityContextHelper {

    /**
     * 
     * @return the authenticated user
     */
    public static Eater getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }

}
