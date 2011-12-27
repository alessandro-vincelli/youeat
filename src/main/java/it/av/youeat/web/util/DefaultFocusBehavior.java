package it.av.youeat.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * @see http://www.nabble.com/Default-Focus-Behavior--td15934889.html
 */
public class DefaultFocusBehavior extends Behavior {
    private static final long serialVersionUID = -4891399118136854774L;

    @Override
    public void bind(Component component) {
        
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderOnLoadJavaScript("document.getElementById('" + component.getMarkupId() + "').focus();");
    }

}