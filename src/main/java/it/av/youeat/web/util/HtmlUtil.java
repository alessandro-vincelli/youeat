/**
 * 
 */
package it.av.youeat.web.util;

import it.av.youeat.web.components.TransparentWebMarkupContainer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class HtmlUtil {

    private HtmlUtil() {
    };

    public static final void fixInitialHtml(final Page page) {
        TransparentWebMarkupContainer html = new TransparentWebMarkupContainer("html");
        page.add(html);

        IModel<String> localeModel = new AbstractReadOnlyModel<String>() {
            public String getObject() {
                return page.getLocale().getLanguage();
            }
        };
        html.add(new AttributeModifier("lang", true, localeModel));
        html.add(new AttributeModifier("xml:lang", true, localeModel));

    }
}
