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

import it.av.youeat.web.commons.YouEatFeedbackPanel;
import it.av.youeat.web.util.HtmlUtil;

import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;

/**
 * Base Page without user session. Contains some commons elements.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class BasePageSimple extends WebPage {

    private static final CompressedResourceReference STYLES_CSS = new CompressedResourceReference(BasePageSimple.class,
            "resources/styles.css");
    private static final CompressedResourceReference STYLES_JQUERY_CSS = new CompressedResourceReference(BasePage.class,
    "resources/jquery-ui-1.8.5.custom.css");
    
    private YouEatFeedbackPanel feedbackPanel;

    /**
     * Construct.
     */
    public BasePageSimple() {
        HtmlUtil.fixInitialHtml(this);
        add(CSSPackageResource.getHeaderContribution(STYLES_CSS));
        add(CSSPackageResource.getHeaderContribution(STYLES_JQUERY_CSS));
        
        feedbackPanel = new YouEatFeedbackPanel("feedBackPanel");
        feedbackPanel.setOutputMarkupId(true);
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);
        
        BookmarkablePageLink goInfo = new BookmarkablePageLink("goInfo", AboutPage.class);
        add(goInfo);
        
        BookmarkablePageLink goPrivacy = new BookmarkablePageLink("goPrivacy", PrivacyPage.class);
        add(goPrivacy);
    }

    public final FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

}
