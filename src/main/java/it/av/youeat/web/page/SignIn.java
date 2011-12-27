/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.web.page;

import it.av.youeat.web.commons.SignInPanel;
import it.av.youeat.web.panel.FacebookLoginPanel;
import it.av.youeat.web.panel.OpenIDLoginPanel;
import it.av.youeat.web.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

/**
 * SignIn page performs authentication on an internal youeat db and on Facebook
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SignIn extends BasePageSimple {

    /**
     * Constructor
     */
    public SignIn() {
        //to eliminate duplicated feedback panel
        getFeedbackPanel().setVisible(false);
        appendToPageTile(" " + getString("basepage.goSignIn"));
        // try a facebook authentication
        ((SecuritySession) getSession()).authenticate((HttpServletRequest) getRequest().getContainerRequest());
        // if facebook authentication sucedeed redirect to home page
        if (getSession().getAuthorizationStrategy().isInstantiationAuthorized(UserHomePage.class)) {
            getRequestCycle().setResponsePage(UserHomePage.class);
        }

        add(new SignInPanel("signInPanel", true));

        add(new AjaxFallbackLink<String>("signUp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SignUpPage.class);
            }
        });

        add(new AjaxFallbackLink<String>("passwordRecover") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(PasswordRecoverPage.class);
            }
        });

        FacebookLoginPanel facebookLoginPanel = new FacebookLoginPanel("facebookSignInPanel");
        // make sure you add the panel first
        add(facebookLoginPanel);
        // now you can create the panel contents
        facebookLoginPanel.createPanel();
        facebookLoginPanel.setEnabled(true);
        
        OpenIDLoginPanel openIDLoginPanel = new OpenIDLoginPanel("openIDSignInPanel");
        // make sure you add the panel first
        add(openIDLoginPanel);
        // now you can create the panel contents
        openIDLoginPanel.createPanel();
        openIDLoginPanel.setEnabled(true);
        
    }

}