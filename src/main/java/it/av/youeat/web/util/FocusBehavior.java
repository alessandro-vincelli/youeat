package it.av.youeat.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * @see http://www.nabble.com/Default-Focus-Behavior--td15934889.html
 */
public class FocusBehavior extends Behavior {
    private static final long serialVersionUID = -4891399118136854774L;


    /**
     * @param component
     */
    public FocusBehavior(Component component) {
        super();

    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        if (!(component instanceof FormComponent)) {
            throw new IllegalArgumentException("DefaultFocusBehavior: component must be instanceof FormComponent");
        }
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderOnLoadJavaScript("document.getElementById('" + component.getMarkupId() + "').focus();");
    }

}