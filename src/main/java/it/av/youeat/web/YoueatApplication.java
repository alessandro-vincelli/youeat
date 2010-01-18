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
package it.av.youeat.web;

import it.av.youeat.web.page.EaterAccountPage;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.ErrorPage;
import it.av.youeat.web.page.FriendsPage;
import it.av.youeat.web.page.HomePage;
import it.av.youeat.web.page.RistoranteEditAddressPage;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.page.SearchFriendPage;
import it.av.youeat.web.page.SignIn;
import it.av.youeat.web.page.SignUpPage;
import it.av.youeat.web.page.UserHomePage;
import it.av.youeat.web.page.UserManagerPage;
import it.av.youeat.web.page.UserProfilePage;
import it.av.youeat.web.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Main class Application.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class YoueatApplication extends AuthenticatedWebApplication {
    @SpringBean
    private String configurationType; 
    
    /**
     * Constructor
     */
    public YoueatApplication() {
    }

    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return SecuritySession.class;
    }

    protected Class<? extends WebPage> getSignInPageClass() {
        return SignIn.class;
    }
    
    @Override
    protected final void init() {
        super.init();
        getMarkupSettings().setStripWicketTags(true); 
        // THIS LINE IS IMPORTANT - IT INSTALLS THE COMPONENT INJECTOR THAT WILL
        // INJECT NEWLY CREATED COMPONENTS WITH THEIR SPRING DEPENDENCIES
        if(getSpringContext() != null){
            addComponentInstantiationListener(new SpringComponentInjector(this, getSpringContext(), true));
        }
        mount(new IndexedParamUrlCodingStrategy("/signIn", SignIn.class));
        mount(new HybridUrlCodingStrategy("/userProfile", UserProfilePage.class));
        mount(new HybridUrlCodingStrategy("/userPage", UserManagerPage.class));
        //mount(new HybridUrlCodingStrategy("/ristorante", RistoranteAddNewPage.class));
        mount(new HybridUrlCodingStrategy("/ristoranteEdit", RistoranteEditAddressPage.class));
        mount(new HybridUrlCodingStrategy("/ristoranteView", RistoranteViewPage.class));
        mount(new HybridUrlCodingStrategy("/searchFriends", SearchFriendPage.class));
        mount(new HybridUrlCodingStrategy("/friends", FriendsPage.class)); 
        mount(new IndexedParamUrlCodingStrategy("/signUp", SignUpPage.class));
        mount(new IndexedParamUrlCodingStrategy("/userHomePage", UserHomePage.class));
        mount(new HybridUrlCodingStrategy("/viewuser", EaterViewPage.class));
        mount(new HybridUrlCodingStrategy("/account", EaterAccountPage.class));
        
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public final Class getHomePage() {
        //If loggedIn different Home Page
        if(getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(UserHomePage.class)){
            return UserHomePage.class;
        }
        return HomePage.class;
    }

    /**
     * @return WebApplicationContext
     */
    public final WebApplicationContext getSpringContext() {
        return WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    }

    @Override
    public String getConfigurationType() {
        return this.configurationType;
    }

    @Override
    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
        return new UploadWebRequest(servletRequest);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {
        return new YouetRequestCycle(this, (WebRequest) request, response);
    }
    
    /**
     * Get Application for current thread.
     *
     * @return The current thread's BBoxApplication
     */
    public static YoueatApplication get() {
        return (YoueatApplication) Application.get();
    }
    
    /**
     * @return true when running in deployment configuration, false for development configuration
     */
    public boolean inDeployment() {
        return DEPLOYMENT.equals(getConfigurationType());
    }

    /**
     * @return true when running in development configuration, false for deployment configuration
     */
    public boolean inDevelopment() {
        return !inDeployment();
    }    
    
    public final void setConfigurationType(String configurationType) {
        this.configurationType = configurationType;
    }

}
