package it.av.youeat.web.security;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Request;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.facebookapi.FacebookJaxbRestClient;
import com.google.code.facebookapi.FacebookParam;

public class FaceBookAuthHandler {

    private static Logger log = LoggerFactory.getLogger(FaceBookAuthHandler.class);

    public static FacebookJaxbRestClient getAuthenticatedClient(Request request, String apiKey, String secretKey)
            throws Exception {
        String authToken = request.getParameter("auth_token");
        String sessionKey = request.getParameter(FacebookParam.SESSION_KEY.toString());
        FacebookJaxbRestClient fbClient = null;
        if (sessionKey != null) {
            fbClient = new FacebookJaxbRestClient(apiKey, secretKey, sessionKey);
        } else if (authToken != null) {
            fbClient = new FacebookJaxbRestClient(apiKey, secretKey);
            // establish session
            fbClient.auth_getSession(authToken);
        } else {
            throw new FailedLoginException("Session key not found");
        }
        // fbClient.setIsDesktop(false);
        return fbClient;
    }

    public static FacebookJaxbRestClient getAuthenticatedClient(HttpServletRequest request, String apiKey,
            String secretKey) throws Exception {
        // FacebookWebRequest<Object> wr = FacebookWebRequest.newInstanceJaxb(request, apiKey, secretKey);
        // boolean logged = wr.isLoggedIn();
        // String sessionKey = wr.getSessionKey();
        FacebookJaxbRestClient fbClient = new FacebookJaxbRestClient(apiKey, secretKey);
        String authToken = request.getParameter("auth_token");
        String sessionJson = request.getParameter("session");
        String sessionKey = getSessionKeyFromJson(sessionJson);
        // fbClient = null;
        if (sessionKey != null) {
            fbClient = new FacebookJaxbRestClient(apiKey, secretKey, sessionKey);
        } else if (authToken != null) {
            fbClient = new FacebookJaxbRestClient(apiKey, secretKey);
            // establish session
            fbClient.auth_getSession(authToken);
        } else {
            throw new FailedLoginException("Session key not found");
        }
        // fbClient.setIsDesktop(false);
        return fbClient;
    }

    private static String getSessionKeyFromJson(String req) {
        if (StringUtils.isNotBlank(req)) {
            try {
                JSONObject json = new JSONObject(req);
                String cacheSessionKey = json.getString("session_key");
                // long cacheUserId = json.getLong( "uid" );
                // long cacheSessionExpires = json.getLong( "expires" );
                return cacheSessionKey;
            } catch (JSONException e) {
                log.info("Session key not found on Json");
            }
        }
        return null;

    }
}
