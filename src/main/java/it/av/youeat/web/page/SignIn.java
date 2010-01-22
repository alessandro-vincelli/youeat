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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SignIn extends BasePageSimple {

    /**
     * Constructor
     */
    public SignIn() {
        getFeedbackPanel().setVisible(false);
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
    }

}