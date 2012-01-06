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

import it.av.youeat.web.page.AboutPage;
import it.av.youeat.web.page.ActivitiesManagerPage;
import it.av.youeat.web.page.EaterAccountPage;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.ErrorPage;
import it.av.youeat.web.page.FriendsPage;
import it.av.youeat.web.page.HomePage;
import it.av.youeat.web.page.ImageViewPage;
import it.av.youeat.web.page.IndexRistoPage;
import it.av.youeat.web.page.MessageListPage;
import it.av.youeat.web.page.MessagePage;
import it.av.youeat.web.page.PasswordRecoverPage;
import it.av.youeat.web.page.PrivacyPage;
import it.av.youeat.web.page.RistoranteAddNewPage;
import it.av.youeat.web.page.RistoranteEditAddressPage;
import it.av.youeat.web.page.RistoranteEditDataPage;
import it.av.youeat.web.page.RistoranteEditPicturePage;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.page.SearchFriendPage;
import it.av.youeat.web.page.SignIn;
import it.av.youeat.web.page.SignOut;
import it.av.youeat.web.page.SignUpPage;
import it.av.youeat.web.page.UserHomePage;
import it.av.youeat.web.page.UserManagerPage;
import it.av.youeat.web.page.UserProfilePage;
import it.av.youeat.web.page.XdReceiver;
import it.av.youeat.web.page.YoueatHttpParams;
import it.av.youeat.web.page.info.InfoForRestaurateurPage;
import it.av.youeat.web.page.manager.CommentsManagerPage;
import it.av.youeat.web.page.manager.RistoranteManagerPage;
import it.av.youeat.web.page.xml.FeedPage;
import it.av.youeat.web.page.xml.SitemapPage;
import it.av.youeat.web.security.SecuritySession;
import it.av.youeat.web.url.YouEatPagePaths;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.IProvider;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Wicket Application.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class YoueatApplication extends AuthenticatedWebApplication {
    @SpringBean
    private String configurationTypeProperty;
    private String applicationURL;
    private String gmapKey;
    private int ristoXPageDataTable = 25;
    
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
    protected void init() {
        super.init(); 

        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setStripWicketTags(true);
        // TODO following line disabled to prevent strip of the adSense code, add differently the adsense and an restore the line below
        //getMarkupSettings().setStripComments(inDeployment());

        // THIS LINE IS IMPORTANT - IT INSTALLS THE COMPONENT INJECTOR THAT WILL
        // INJECT NEWLY CREATED COMPONENTS WITH THEIR SPRING DEPENDENCIES
        if(getSpringContext() != null){
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, getSpringContext(), true));
        }
        mountPage("/info", AboutPage.class);
        mountPage("/InfoForRestaurateur", InfoForRestaurateurPage.class);
        mountPage("/signIn", SignIn.class);
        mountPage("/signOut", SignOut.class);
        mountPage("/userProfile", UserProfilePage.class);
        mountPage("/userPage", UserManagerPage.class);
        mountPage("/newRistorante", RistoranteAddNewPage.class);
        mountPage("/editRistorante" + "/${" + YoueatHttpParams.RISTORANTE_ID + "}/" , RistoranteEditDataPage.class);
        mountPage("/editAddressRistorante" + "/${" + YoueatHttpParams.RISTORANTE_ID + "}/", RistoranteEditAddressPage.class);
        mountPage("/editPicturesRistorante" + "/${" + YoueatHttpParams.RISTORANTE_ID + "}/", RistoranteEditPicturePage.class);
        mountPage(YouEatPagePaths.VIEW_RISTORANTE + "/${" + YoueatHttpParams.RISTORANTE_NAME_AND_CITY + "}/", RistoranteViewPage.class);
        mountPage("/searchFriends", SearchFriendPage.class);
        mountPage("/friends", FriendsPage.class);
        mountPage("/signUp", SignUpPage.class);
        mountPage("/userHomePage", UserHomePage.class);
        mountPage(YouEatPagePaths.VIEW_EATER + "/${" + YoueatHttpParams.YOUEAT_ID + "}/", EaterViewPage.class);
        mountPage("/account" + "/${" + YoueatHttpParams.YOUEAT_ID + "}/", EaterAccountPage.class);
        mountPage("/passwordRecover", PasswordRecoverPage.class);
        mountPage("/messages", MessageListPage.class);
        mountPage("/message" + "/${" + YoueatHttpParams.DIALOG_ID + "}/" , MessagePage.class);
        mountPage("/picture", ImageViewPage.class);
        mountPage("/index", IndexRistoPage.class);
        mountPage("/xd_receiver.htm", XdReceiver.class);
        mountPage("/privacy", PrivacyPage.class);
        mountPage("/sitemap.xml", SitemapPage.class);
        mountPage("/feed", FeedPage.class);
        mountPage("/ristoManager", RistoranteManagerPage.class);
        mountPage("/activitiesManager", ActivitiesManagerPage.class);
        mountPage("/commentsManager", CommentsManagerPage.class);
        
//        mount(new IndexedParamUrlCodingStrategy("/info", AboutPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/InfoForRestaurateur", InfoForRestaurateurPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/signIn", SignIn.class));
//        mount(new IndexedParamUrlCodingStrategy("/signOut", SignOut.class));
//        mount(new HybridUrlCodingStrategy("/userProfile", UserProfilePage.class));
//        mount(new HybridUrlCodingStrategy("/userPage", UserManagerPage.class));
//        mount(new HybridUrlCodingStrategy("/newRistorante", RistoranteAddNewPage.class));
//        mount(new MixedParamHybridUrlCodingStrategy("/editRistorante", RistoranteEditDataPage.class, new String[]{YoueatHttpParams.RISTORANTE_ID}));
//        mount(new MixedParamHybridUrlCodingStrategy("/editAddressRistorante", RistoranteEditAddressPage.class, new String[]{YoueatHttpParams.RISTORANTE_ID}));
//        mount(new MixedParamHybridUrlCodingStrategy("/editPicturesRistorante", RistoranteEditPicturePage.class, new String[]{YoueatHttpParams.RISTORANTE_ID}));
//        mount(new MixedParamUrlCodingStrategy(YouEatPagePaths.VIEW_RISTORANTE, RistoranteViewPage.class, new String[]{YoueatHttpParams.RISTORANTE_NAME_AND_CITY}));
//        mount(new HybridUrlCodingStrategy("/searchFriends", SearchFriendPage.class));
//        mount(new HybridUrlCodingStrategy("/friends", FriendsPage.class)); 
//        mount(new IndexedParamUrlCodingStrategy("/signUp", SignUpPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/userHomePage", UserHomePage.class));
//        mount(new MixedParamHybridUrlCodingStrategy(YouEatPagePaths.VIEW_EATER, EaterViewPage.class, new String[]{YoueatHttpParams.YOUEAT_ID}));
//        mount(new MixedParamHybridUrlCodingStrategy("/account", EaterAccountPage.class, new String[]{YoueatHttpParams.YOUEAT_ID}));
//        mount(new HybridUrlCodingStrategy("/passwordRecover", PasswordRecoverPage.class));
//        mount(new HybridUrlCodingStrategy("/messages", MessageListPage.class));
//        mount(new MixedParamHybridUrlCodingStrategy("/message", MessagePage.class, new String[]{YoueatHttpParams.DIALOG_ID}));
//        mount(new HybridUrlCodingStrategy("/picture", ImageViewPage.class));
//        mount(new QueryStringUrlCodingStrategy("/index", IndexRistoPage.class));
//        mount(new MixedParamUrlCodingStrategy("/xd_receiver.htm", XdReceiver.class, null));
//        mount(new IndexedParamUrlCodingStrategy("/privacy", PrivacyPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/sitemap.xml", SitemapPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/feed", FeedPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/ristoManager", RistoranteManagerPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/activitiesManager", ActivitiesManagerPage.class));
//        mount(new IndexedParamUrlCodingStrategy("/commentsManager", CommentsManagerPage.class));
        
        
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);
        
        setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig(80, 443)));
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
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.valueOf(configurationTypeProperty.toUpperCase());
    }
//    //TODO 1.5
//    @Override
//    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
//        return new UploadWebRequest(servletRequest);
//    }
//    
//    /**
//     * {@inheritDoc}
//     */
    //TODO 1.5
//    @Override
//    public RequestCycle newRequestCycle(Request request, Response response) {
//        return new YouetRequestCycle(this, (WebRequest) request, response);
//    }
    
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
        return getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT);
    }

    /**
     * @return true when running in development configuration, false for deployment configuration
     */
    public boolean inDevelopment() {
        return getConfigurationType().equals(RuntimeConfigurationType.DEVELOPMENT);
    }    
    
    public final void setConfigurationTypeProperty(String configurationTypeProperty) {
        this.configurationTypeProperty = configurationTypeProperty;
    }

    public String getApplicationURL() {
        return applicationURL;
    }

    public void setApplicationURL(String applicationURL) {
        this.applicationURL = applicationURL;
    }

    public String getGmapKey() {
        return gmapKey;
    }

    public void setGmapKey(String gmapKey) {
        this.gmapKey = gmapKey;
    }

    public int getRistoXPageDataTable() {
        return ristoXPageDataTable;
    }

    @Override
    public IProvider<IExceptionMapper> getExceptionMapperProvider() {
        return super.getExceptionMapperProvider();
    }

    public void setRistoXPageDataTable(int ristoXPageDataTable) {
        this.ristoXPageDataTable = ristoXPageDataTable;
    }

}