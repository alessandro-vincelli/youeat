package it.av.youeat.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * @see http://www.nabble.com/Default-Focus-Behavior--td15934889.html
 */
public class DefaultFocusBehavior extends AbstractBehavior {
    private static final long serialVersionUID = -4891399118136854774L;

    private Component component;

    @Override
    public void bind(Component component) {
        this.component = component;
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        super.renderHead(iHeaderResponse);
        iHeaderResponse.renderOnLoadJavascript("document.getElementById('" + component.getMarkupId() + "').focus();");
    }
}