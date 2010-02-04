package it.av.youeat.web.panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJsonRestClient;
import com.google.code.facebookapi.FacebookWebappHelper;
import com.google.code.facebookapi.ProfileField;
import com.google.code.facebookapi.schema.User;


public class FacebookLoginPanel extends Panel {

        private final Log log = LogFactory.getLog(FacebookLoginPanel.class);
        private WebMarkupContainer fbloginDiv;
        private Label fblogin;
        
        public FacebookLoginPanel(String id) {
            super(id);
            // TODO Auto-generated constructor stub
        }
        
        /**
         * This method will the panel
         */
        public void createPanel() {
            Form facebookSignInForm = new Form("facebookSignInForm");
            add(facebookSignInForm);
            facebookSignInForm.add(new AjaxFallbackLink<String>("facebookSignInButton"){

                @Override
                public void onClick(AjaxRequestTarget target) {
                    getRequestCycle().setRequestTarget(new RedirectRequestTarget("http://www.facebook.com/login.php?api_key=083c31f005625c34a27aa011a279322b&extern=1&fbconnect=1&req_perms=publish_stream&return_session=1&v=1.0&next=http://www.youeat.org/signIn"));
                }
                
            });
        }
        
        /**
         * All that we do to log you in from facebook. I put my fbook.key and fbook.secret in the 
         * properties file.
         * @param thePage
         */
        public void handleFacebookCallback(Page thePage) {
            
            HttpServletRequest req = ((ServletWebRequest) thePage.getRequest()).getHttpServletRequest();
            HttpServletResponse res = ((WebResponse) thePage.getResponse()).getHttpServletResponse();
            String api = getLocalizer().getString("fbook.key", this);
            String secret = getLocalizer().getString("fbook.secret", this);
            FacebookWebappHelper<Object> helper = FacebookWebappHelper.newInstanceJson(req, res, api, secret);
            
            // make sure the login worked
            if (helper.isLogin()) {
                FacebookJsonRestClient facebookClient = (FacebookJsonRestClient) helper.getFacebookRestClient();
                long id;
                try {
                    // grab the logged in user's id
                    id = facebookClient.users_getLoggedInUser();

                    // you can bundle ajax calls...
                    facebookClient.beginBatch();
                    
                    // i'm going to call the users.getInfo fb api call, just to make sure it works
                    ArrayList<Long> ids = new ArrayList<Long>();
                    ids.add(new Long(id));

                    // put together a set of fields for fb to return
                    HashSet<ProfileField> fields = new HashSet<ProfileField>();
                    fields.add(ProfileField.FIRST_NAME);
                    fields.add(ProfileField.LAST_NAME);

                    // get the user data
                    facebookClient.users_getInfo(ids, fields);
                    
                    // execute the batch (which also terminates batch mode until beginBatch is called again)
                    List<? extends Object> batchResponse = facebookClient.executeBatch(false);
                    JSONArray userInfo = (JSONArray) batchResponse.get(0);
                    JSONObject user = userInfo.getJSONObject(0);

                    // a pojo user object
                    User theUser = new User();
                    String username = user.getString("first_name");
//                    theUser.setUsername(username);
//                    
//                    // fb emails are proxy, my app needs some kind of holder
//                    theUser.setEmail("noreply@facebook.com");
//                    theUser.setUserId(new Integer(0));
//                    theUser.setFacebook(true);
//                    theUser.setFacebookId(id);
//                    
//                    // we use spring, so here we give basic access to the facebook user.
//                    List<GrantedAuthority> gaList = new ArrayList<GrantedAuthority>();
//                    gaList.add(new GrantedAuthorityImpl("STANDARD"));
//                    theUser.setAuthorities(gaList.toArray(new GrantedAuthority[] {}));
//                    GrantedAuthority[] ga = theUser.getAuthorities();
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(theUser, theUser, ga);
//                    SecurityContext context = new SecurityContextImpl();
//                    context.setAuthentication(authentication);
//                    SecurityContextHolder.setContext(context);
                    
                } catch (FacebookException e) {
                    log.error("facebook issues: " + e);
                } catch (JSONException e) {
                    log.error("facebook json issues: " + e);
                }
            }
        }
        
        /**
         * Do your own kind of auth check
         * @return
         */
        public boolean isAuthenticated() {
            return SecurityContextHolder.getContext().getAuthentication() != null;
        }

    }
