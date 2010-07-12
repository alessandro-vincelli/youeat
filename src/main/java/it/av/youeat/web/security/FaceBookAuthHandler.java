package it.av.youeat.web.security;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class FaceBookAuthHandler {

    private String apiKey;
    private String secret;
    
    public FacebookJaxbRestClient getAuthenticatedClient(HttpServletRequest request) throws FailedLoginException, FacebookException, JSONException {
        String cacheSessionKey = "";
        long cacheUserId = 0;
        long cacheSessionExpires = 0;
        String authToken = request.getParameter("auth_token");
        String sessionJson = request.getParameter("session");
        // try to get session onfio from json request from FB
        if (StringUtils.isNotBlank(sessionJson)) {
            JSONObject json = new JSONObject(sessionJson);
            cacheSessionKey = json.getString("session_key");
            cacheUserId = json.getLong("uid");
            cacheSessionExpires = json.getLong("expires");
        }
        FacebookJaxbRestClient fbClient = new FacebookJaxbRestClient(apiKey, secret);
        if (StringUtils.isNotBlank(cacheSessionKey) && cacheUserId != 0) {
            fbClient.setCacheSession(cacheSessionKey, cacheUserId, cacheSessionExpires);
        } else if (authToken != null) {
            // establish session
            fbClient.auth_getSession(authToken);
        } else {
            throw new FailedLoginException("Session key not found");
        }
        // fbClient.setIsDesktop(false);
        return fbClient;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
