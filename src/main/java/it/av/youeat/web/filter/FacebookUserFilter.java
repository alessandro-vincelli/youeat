package it.av.youeat.web.filter;

import it.av.youeat.web.security.FaceBookAuthHandler;
import it.av.youeat.web.security.FacebookNumbers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;
import com.google.code.facebookapi.FacebookWebappHelper;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.IFacebookRestClient;
import com.google.code.facebookapi.ProfileField;
import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;
import com.google.code.facebookapi.schema.User.EmailHashes;

public class FacebookUserFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(FacebookUserFilter.class);

    private String api_key = FacebookNumbers.apiKey;
    private String secret = FacebookNumbers.secret;

    private static final String FACEBOOK_USER_CLIENT = "facebook.user.client";

    public void init(FilterConfig filterConfig) throws ServletException {
//        api_key = filterConfig.getServletContext().getInitParameter("facebook_api_key");
//        secret = filterConfig.getServletContext().getInitParameter("facebook_secret");
//        if (api_key == null || secret == null) {
//            throw new ServletException("Cannot initialise Facebook User Filter because the "
//                    + "facebook_api_key or facebook_secret context init "
//                    + "params have not been set. Check that they're there " + "in your servlet context descriptor.");
//        } else {
//            logger.info("Using facebook API key: " + api_key);
//        }
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        try {
            // MDC.put(ipAddress, req.getRemoteAddr());

            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            HttpSession session = request.getSession(true);
            IFacebookRestClient<Document> userClient = getUserClient(session);
            if (userClient == null) {
                logger
                        .debug("User session doesn't have a Facebook API client setup yet. Creating one and storing it in the user's session.");
                userClient = new FacebookXmlRestClient(api_key, secret);
                session.setAttribute(FACEBOOK_USER_CLIENT, userClient);
            }

            logger.trace("Creating a FacebookWebappHelper, which copies fb_ request param data into the userClient");
            FacebookWebappHelper<Document> facebook = new FacebookWebappHelper<Document>(request, response, api_key,
                    secret, userClient);
            String nextPage = request.getRequestURI();
            nextPage = nextPage.substring(nextPage.indexOf("/", 1) + 1); // cut out the first /, the context path and
            // the 2nd /
            logger.trace(nextPage);
            try {
                FacebookJaxbRestClient authClient = FaceBookAuthHandler.getAuthenticatedClient(request, api_key, secret);
                long userId = authClient.users_getLoggedInUser();
                ArrayList<Long> ids = new ArrayList<Long>();
                ids.add(userId);
                ArrayList<ProfileField> fields = new ArrayList<ProfileField>();
                fields.add(ProfileField.FIRST_NAME);
                fields.add(ProfileField.LAST_NAME);
                fields.add(ProfileField.EMAIL_HASHES);
                UsersGetInfoResponse users = authClient.users_getInfo(ids, fields);
                User user = users.getUser().get(0);
                String name = user.getFirstName();
                EmailHashes email = user.getEmailHashes();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            boolean redirectOccurred = facebook.requireLogin(nextPage);
            if (redirectOccurred) {
                return;
            }
            redirectOccurred = facebook.requireFrame(nextPage);
            if (redirectOccurred) {
                return;
            }

            long facebookUserID;
            try {
                facebookUserID = userClient.users_getLoggedInUser();
            } catch (FacebookException ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error while fetching user's facebook ID");
                logger.error("Error while getting cached (supplied by request params) value "
                        + "of the user's facebook ID or while fetching it from the Facebook service "
                        + "if the cached value was not present for some reason. Cached value = {}", userClient
                        .getCacheUserId());
                return;
            }

            // MDC.put(facebookUserId, String.valueOf(facebookUserID));

            chain.doFilter(request, response);
        } finally {
            // MDC.remove(ipAddress);
            // MDC.remove(facebookUserId);
        }
    }

    public static FacebookXmlRestClient getUserClient(HttpSession session) {
        return (FacebookXmlRestClient) session.getAttribute(FACEBOOK_USER_CLIENT);
    }
}