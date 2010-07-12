package it.av.youeat.web.security;

import it.av.youeat.ocm.model.Eater;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class FacebookAuthenticationToken extends AbstractAuthenticationToken {

    private Object request;

    /**
     * Constructor
     * 
     * @param request
     */
    public FacebookAuthenticationToken(Object request) {
        super(null);
        this.request = request;
        setAuthenticated(false);
    }
    
    @Override
    public Object getCredentials() {
        return request;
    }

    @Override
    public Object getPrincipal() {
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        GrantedAuthority ga = new GrantedAuthorityImpl(((Eater) getPrincipal()).getUserProfile().getName());
        ArrayList<GrantedAuthority> gaL = new ArrayList<GrantedAuthority>(1);
        gaL.add(ga);
        return gaL;
    }

}
