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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.service.MessageService;
import it.av.youeat.web.Locales;
import it.av.youeat.web.commons.CookieUtil;
import it.av.youeat.web.commons.YouEatFeedbackPanel;
import it.av.youeat.web.page.info.InfoForRestaurateurPage;
import it.av.youeat.web.page.manager.CommentsManagerPage;
import it.av.youeat.web.page.manager.RistoranteManagerPage;
import it.av.youeat.web.page.xml.FeedPage;
import it.av.youeat.web.security.SecuritySession;
import it.av.youeat.web.util.HtmlUtil;

import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Contains some commons elements.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class BasePage extends WebPage {

    
    private static final String BASEPAGE_JS =  "BasePage.js";
    private static final String STYLES_CSS =  "resources/styles.css";
    private static final String STYLES_JQUERY_CSS =  "resources/jquery-ui-1.8.5.custom.css";
    
    private YouEatFeedbackPanel feedbackPanel;
    private boolean isAuthenticated = false;
    private Eater loggedInUser = null;
    @SpringBean
    private MessageService messageService;
    @SpringBean
    private EaterRelationService eaterRelationService;
    private Label titlePage;

    /**
     * Construct.
     */
    public BasePage() {
        SecuritySession securitySession = ((SecuritySession) getSession());
        if (securitySession.getAuth() != null
                && securitySession.getAuth().isAuthenticated()) {
            isAuthenticated = true;
        }

        loggedInUser = securitySession.getLoggedInUser();
        if(((ServletWebRequest)RequestCycle.get().getRequest()).getCookie(CookieUtil.LANGUAGE) != null) {
            getSession().setLocale(
                    new Locale(((ServletWebRequest)RequestCycle.get().getRequest()).getCookie(CookieUtil.LANGUAGE).getValue()));
        } else {
            if (loggedInUser != null) {
                ((ServletWebResponse)RequestCycle.get().getResponse()).addCookie((new Cookie(CookieUtil.LANGUAGE, loggedInUser.getLanguage().getLanguage())));
                getSession().setLocale(new Locale(loggedInUser.getLanguage().getLanguage()));
            }
        }
        HtmlUtil.fixInitialHtml(this);
        titlePage = new Label("pageTitle", ":: YouEat ::");
        add(titlePage);
        feedbackPanel = new YouEatFeedbackPanel("feedBackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);
        
        add(new BookmarkablePageLink<String>("goUserPage", UserManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserManagerPage.class)));
            }
        });

        add(new BookmarkablePageLink<String>("goUserProfilePage", UserProfilePage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserProfilePage.class)));
            }
        });

        add(new BookmarkablePageLink<String>("goSearchRistorantePage", UserHomePage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserHomePage.class)));
            }
        });

        add(new BookmarkablePageLink<String>("goRistoranteAddNewPage", RistoranteAddNewPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(RistoranteAddNewPage.class)));
            }
        });

        long numberPendingFrienRequest = (getLoggedInUser() != null) ? eaterRelationService
                .getAllPendingFriendRequetToUsers(getLoggedInUser()).size() : 0;
        final Label pendingFriendRequests = new Label("pendingFriendRequests", new Model<String>("("
                + numberPendingFrienRequest + ")"));
        pendingFriendRequests.setOutputMarkupPlaceholderTag(true);
        pendingFriendRequests.setVisible(numberPendingFrienRequest > 0);
        add(new BookmarkablePageLink<String>("goFriendPage", FriendsPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(FriendsPage.class)));
            }
        }.add(pendingFriendRequests));

        BookmarkablePageLink<String> goMessagesPage = new BookmarkablePageLink<String>("goMessagesPage", MessageListPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(MessageListPage.class)));
            }
        };
        add(goMessagesPage);
        long numberofMessages = (getLoggedInUser() != null) ? messageService.countIncomingMessages(getLoggedInUser())
                : 0;
        final Label numberMessages = new Label("numberMessages", new Model<Long>(numberofMessages));
        numberMessages.setOutputMarkupPlaceholderTag(true);
        // numberMessages.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
        // @Override
        // protected void onPostProcessTarget(AjaxRequestTarget target) {
        // super.onPostProcessTarget(target);
        // long numberMessagesRefreshed = messageService.countMessages(getLoggedInUser());
        // numberMessages.setDefaultModelObject((Long) numberMessagesRefreshed);
        // numberMessages.setVisible(numberMessagesRefreshed > 0);
        // if (target != null) {
        // // target.appendJavascript("new Effect.Highlight($('" + numberMessages.getMarkupId() + "'));");
        // }
        // }
        // });
        numberMessages.setVisible(numberofMessages > 0);
        goMessagesPage.add(numberMessages);

        long numberUnreadMsgs = (getLoggedInUser() != null) ? messageService.countUnreadMessages(getLoggedInUser()) : 0;

        final WebMarkupContainer separator = new WebMarkupContainer("separator");
        separator.setVisible(numberUnreadMsgs > 0);
        goMessagesPage.add(separator);

        final Label unreadMsgs = new Label("unreadMessages", new Model<Long>(numberUnreadMsgs));
        unreadMsgs.setOutputMarkupPlaceholderTag(true);
        unreadMsgs.setVisible(numberUnreadMsgs > 0);
        // unreadMsgs.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
        // @Override
        // protected void onPostProcessTarget(AjaxRequestTarget target) {
        // super.onPostProcessTarget(target);
        // long numberUnreadMessagesRefreshed = messageService.countUnreadMessages(getLoggedInUser());
        // unreadMsgs.setDefaultModelObject((Long) numberUnreadMessagesRefreshed);
        // if (target != null) {
        // // target.appendJavascript("new Effect.Highlight($('" + unreadMsgs.getMarkupId() + "'));");
        // }
        // }
        // });
        goMessagesPage.add(unreadMsgs);

        Link<String> goItalian = new Link<String>("goItalian") {
            @Override
            public void onClick() {
                getSession().setLocale(Locales.ITALIAN);
                ((ServletWebResponse)RequestCycle.get().getResponse()).addCookie((
                        new Cookie(CookieUtil.LANGUAGE, Locales.ITALIAN.getLanguage())));
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (getSession().getLocale().getLanguage().equals(Locales.ITALIAN.getLanguage())) {
                    tag.getAttributes().put("class", "selected");
                }
            }
        };
        add(goItalian);

        Link<String> goEnglish = new Link<String>("goEnglish") {
            @Override
            public void onClick() {
                getSession().setLocale(Locales.ENGLISH);
                ((ServletWebResponse)RequestCycle.get().getResponse()).addCookie((
                        new Cookie(CookieUtil.LANGUAGE, Locales.ENGLISH.getLanguage())));
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (getSession().getLocale().getLanguage().equals(Locales.ENGLISH.getLanguage())) {
                    tag.getAttributes().put("class", "selected");
                }
            }
        };
        add(goEnglish);

//        Link<String> goDutch = new Link<String>("goDutch") {
//            @Override
//            public void onClick() {
//                getWebRequestCycle().getWebResponse().addCookie(
//                        new Cookie(CookieUtil.LANGUAGE, Locales.DUTCH.getLanguage()));
//                getSession().setLocale(Locales.DUTCH);
//            }
//
//            @Override
//            protected void onComponentTag(ComponentTag tag) {
//                super.onComponentTag(tag);
//                if (getSession().getLocale().getLanguage().equals(Locales.DUTCH.getLanguage())) {
//                    tag.getAttributes().put("class", "selected");
//                }
//            }
//        };
//        add(goDutch);

        BookmarkablePageLink goSignOut = new BookmarkablePageLink("goSignOut", SignOut.class);

        BookmarkablePageLink goSignIn = new BookmarkablePageLink("goSignIn", SignIn.class);

        goSignIn.setOutputMarkupId(true);
        goSignOut.setOutputMarkupId(true);

        if (isAuthenticated) {
            goSignOut.setVisible(true);
            goSignIn.setVisible(false);
            // goSignUp.setVisible(false);
        } else {
            goSignOut.setVisible(false);
            goSignIn.setVisible(true);
            // goSignUp.setVisible(true);
        }
        add(goSignOut);
        add(goSignIn);
        // add(goSignUp);

        PageParameters goAccountParameters = new PageParameters();
        if (loggedInUser != null) {
            goAccountParameters.add(YoueatHttpParams.YOUEAT_ID, loggedInUser.getId());
        }

        BookmarkablePageLink goAccount;
        if (loggedInUser != null && loggedInUser.isSocialNetworkEater()) {
            goAccount = new BookmarkablePageLink<String>("goAccount", EaterFacebookAccountPage.class,
                    goAccountParameters);
        } else {
            goAccount = new BookmarkablePageLink<String>("goAccount", EaterAccountPage.class, goAccountParameters);
        }
        goAccount.setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                .isInstantiationAuthorized(EaterAccountPage.class)));

        Label name = new Label("name", loggedInUser != null ? loggedInUser.toString() : "");
        goAccount.add(name);
        add(goAccount);

        BookmarkablePageLink goInfo = new BookmarkablePageLink("goInfo", AboutPage.class);
        add(goInfo);
        
        BookmarkablePageLink goInfoTop = new BookmarkablePageLink("goInfoTop", AboutPage.class);
        add(goInfoTop);

        BookmarkablePageLink goIndex = new BookmarkablePageLink("goIndex", IndexRistoPage.class);
        add(goIndex);
        
        BookmarkablePageLink goPrivacy = new BookmarkablePageLink("goPrivacy", PrivacyPage.class);
        add(goPrivacy);
        
        BookmarkablePageLink goFeed = new BookmarkablePageLink("goFeed", FeedPage.class);
        add(goFeed);
        
        BookmarkablePageLink goInfoRestaurateurPage = new BookmarkablePageLink("goInfoRestaurateur", InfoForRestaurateurPage.class){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(!isAuthenticated);
            }
        };
        add(goInfoRestaurateurPage);

        add(new BookmarkablePageLink<String>("goRistoManager", RistoranteManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(RistoranteManagerPage.class)));
            }
        });
        
        add(new BookmarkablePageLink<String>("activitiesManager", ActivitiesManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(ActivitiesManagerPage.class)));
            }
        });
        
        add(new BookmarkablePageLink<String>("commentsManager", CommentsManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(CommentsManagerPage.class)));
            }
        });

    }

    public final FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

    public final Eater getLoggedInUser() {
        return loggedInUser;
    }
    
    protected void setPageTitle(Label titlePage) {
        this.titlePage = titlePage;
    }
    
    protected Label getPageTitle() {
        return titlePage;
    }
    
    protected void appendToPageTile(String title){
        titlePage.setDefaultModelObject(titlePage.getDefaultModelObjectAsString().concat(title));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(new PackageResourceReference(BasePage.class, BASEPAGE_JS));
        response.renderCSSReference(new PackageResourceReference(BasePage.class, STYLES_JQUERY_CSS));
        response.renderCSSReference(new PackageResourceReference(BasePage.class, STYLES_CSS));
    }
    
    
}
