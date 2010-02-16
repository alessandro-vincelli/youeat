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
import it.av.youeat.web.security.SecuritySession;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.protocol.http.WebRequest;

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
        
        // try a facebook authentication
        ((SecuritySession) getSession()).authenticate(((WebRequest) getRequest()).getHttpServletRequest());
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

        FacebookLoginPanel myPanel = new FacebookLoginPanel("facebookSignInPanel");
        // make sure you add the panel first
        add(myPanel);
        // now you can create the panel contents
        myPanel.createPanel();
        myPanel.setEnabled(true);

    }

}