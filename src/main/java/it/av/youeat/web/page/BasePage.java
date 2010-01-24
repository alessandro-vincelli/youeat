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
import it.av.youeat.service.MessageService;
import it.av.youeat.web.Locales;
import it.av.youeat.web.commons.CookieUtil;
import it.av.youeat.web.security.SecuritySession;

import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * Contains some commons elements.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class BasePage extends WebPage {

    // private static final CompressedResourceReference BASEPAGE_JS = new CompressedResourceReference(BasePage.class,
    // "BasePage.js");
    private static final CompressedResourceReference STYLES_CSS = new CompressedResourceReference(BasePage.class,
            "resources/styles.css");
    private FeedbackPanel feedbackPanel;
    private boolean isAuthenticated = false;
    private Eater loggedInUser = null;
    @SpringBean
    private MessageService messageService;

    /**
     * Construct.
     */
    public BasePage() {
        if (((SecuritySession) getSession()).getAuth() != null
                && ((SecuritySession) getSession()).getAuth().isAuthenticated()) {
            isAuthenticated = true;
        }

        loggedInUser = ((SecuritySession) getSession()).getLoggedInUser();
        if (getWebRequestCycle().getWebRequest().getCookie(CookieUtil.LANGUAGE) != null) {
            getSession().setLocale(
                    new Locale(getWebRequestCycle().getWebRequest().getCookie(CookieUtil.LANGUAGE).getValue()));
        } else {
            if (loggedInUser != null) {
                getWebRequestCycle().getWebResponse().addCookie(
                        new Cookie(CookieUtil.LANGUAGE, loggedInUser.getLanguage().getLanguage()));
                getSession().setLocale(new Locale(loggedInUser.getLanguage().getLanguage()));
            }
        }

        // add(JavascriptPackageResource.getHeaderContribution(BASEPAGE_JS));
        add(CSSPackageResource.getHeaderContribution(STYLES_CSS));

        feedbackPanel = new FeedbackPanel("feedBackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);

        ResourceReference img = new ResourceReference(this.getClass(), "resources/images/logo-mela-small.png");
        add(new Image("logo", img));
        add(new AjaxFallbackLink<String>("goUserPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        UserManagerPage.class)) {
                    setResponsePage(UserManagerPage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserManagerPage.class)));
            }
            /*To Show a JS alert on protected area
             * @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new AjaxCallDecorator() {
                    private static final long serialVersionUID = 1L;
                    public CharSequence decorateScript(CharSequence script) {
                        if (!(getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(UserManagerPage.class))) {
                            return "alert('" + new StringResourceModel("basePage.notLogged", getPage(), null).getString() + "'); " + script;
                        }
                        return script;
                    }
                };
            }*/
        });

        add(new AjaxFallbackLink<String>("goUserProfilePage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        UserProfilePage.class)) {
                    setResponsePage(UserProfilePage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserProfilePage.class)));
            }
        });

        add(new AjaxFallbackLink<String>("goSearchRistorantePage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        UserHomePage.class)) {
                    setResponsePage(UserHomePage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserHomePage.class)));
            }
        });

        add(new AjaxFallbackLink<String>("goRistoranteAddNewPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        RistoranteAddNewPage.class)) {
                    setResponsePage(RistoranteAddNewPage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(RistoranteAddNewPage.class)));
            }
        });

        add(new AjaxFallbackLink<String>("goFriendPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        FriendsPage.class)) {
                    setResponsePage(FriendsPage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(FriendsPage.class)));
            }
        });

        AjaxFallbackLink<String> goMessagesPage = new AjaxFallbackLink<String>("goMessagesPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                        MessageListPage.class)) {
                    setResponsePage(MessageListPage.class);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(MessageListPage.class)));
            }
        };
        add(goMessagesPage);
        long numberofMessages = (getLoggedInUser() != null) ? messageService.countMessages(getLoggedInUser()) : 0;
        final Label numberMessages = new Label("numberMessages", new Model<Long>(numberofMessages));
        numberMessages.setOutputMarkupPlaceholderTag(true);
//        numberMessages.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
//            @Override
//            protected void onPostProcessTarget(AjaxRequestTarget target) {
//                super.onPostProcessTarget(target);
//                long numberMessagesRefreshed = messageService.countMessages(getLoggedInUser());
//                numberMessages.setDefaultModelObject((Long) numberMessagesRefreshed);
//                numberMessages.setVisible(numberMessagesRefreshed > 0);
//                if (target != null) {
//                    // target.appendJavascript("new Effect.Highlight($('" + numberMessages.getMarkupId() + "'));");
//                }
//            }
//        });
        numberMessages.setVisible(numberofMessages > 0);
        goMessagesPage.add(numberMessages);

        long numberUnreadMsgs = (getLoggedInUser() != null) ? messageService.countUnreadMessages(getLoggedInUser()) : 0;

        final WebMarkupContainer separator = new WebMarkupContainer("separator");
        separator.setVisible(numberUnreadMsgs > 0);
        goMessagesPage.add(separator);

        final Label unreadMsgs = new Label("unreadMessages", new Model<Long>(numberUnreadMsgs));
        unreadMsgs.setOutputMarkupPlaceholderTag(true);
        unreadMsgs.setVisible(numberUnreadMsgs > 0);
//        unreadMsgs.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
//            @Override
//            protected void onPostProcessTarget(AjaxRequestTarget target) {
//                super.onPostProcessTarget(target);
//                long numberUnreadMessagesRefreshed = messageService.countUnreadMessages(getLoggedInUser());
//                unreadMsgs.setDefaultModelObject((Long) numberUnreadMessagesRefreshed);
//                if (target != null) {
//                    // target.appendJavascript("new Effect.Highlight($('" + unreadMsgs.getMarkupId() + "'));");
//                }
//            }
//        });
        goMessagesPage.add(unreadMsgs);

        Link<String> goItalian = new Link<String>("goItalian") {
            @Override
            public void onClick() {
                getSession().setLocale(Locales.ITALIAN);
                getWebRequestCycle().getWebResponse().addCookie(
                        new Cookie(CookieUtil.LANGUAGE, Locales.ITALIAN.getLanguage()));
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
                getWebRequestCycle().getWebResponse().addCookie(
                        new Cookie(CookieUtil.LANGUAGE, Locales.ENGLISH.getLanguage()));
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

        Link<String> goDutch = new Link<String>("goDutch") {
            @Override
            public void onClick() {
                getWebRequestCycle().getWebResponse().addCookie(
                        new Cookie(CookieUtil.LANGUAGE, Locales.DUTCH.getLanguage()));
                getSession().setLocale(Locales.DUTCH);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (getSession().getLocale().getLanguage().equals(Locales.DUTCH.getLanguage())) {
                    tag.getAttributes().put("class", "selected");
                }
            }
        };
        add(goDutch);

        Link<String> goSignOut = new Link<String>("goSignOut") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SignOut.class);
            }
        };

        Link<String> goSignIn = new Link<String>("goSignIn") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SignIn.class);
            }
        };

        goSignIn.setOutputMarkupId(true);
        goSignOut.setOutputMarkupId(true);

        if (isAuthenticated) {
            goSignIn.setVisible(false);
            goSignOut.setVisible(true);
        } else {
            goSignOut.setVisible(false);
            goSignIn.setVisible(true);
        }
        add(goSignOut);
        add(goSignIn);

        Link<String> goAccount = new Link<String>("goAccount") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                Eater eater = getLoggedInUser();
                if (eater != null) {
                    PageParameters pp = new PageParameters(YoueatHttpParams.YOUEAT_ID + "=" + eater.getId());
                    setResponsePage(EaterAccountPage.class, pp);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(EaterAccountPage.class)));
            }

            @Override
            protected boolean callOnBeforeRenderIfNotVisible() {
                return true;
            }
        };        
        Label name = new Label("name", loggedInUser != null ? loggedInUser.toString() : "");
        goAccount.add(name);
        add(goAccount);

    }

    public final FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

    public Eater getLoggedInUser() {
        return loggedInUser;
    }

}
