package it.av.youeat.web.components;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * A WebMarkupContainer that has transparent resolving.
 * <p>
 * With this container one can add behavior to a specific HTML element
 * (for example to dynamically set a attribute) without the need
 * to add childs directly to this container (i.e. adding to the parent
 * will work as well).
 *
 * @author Erik van Oosten
 */
public class TransparentWebMarkupContainer extends WebMarkupContainer {

    public TransparentWebMarkupContainer(String id) {
        super(id);
    }

    public TransparentWebMarkupContainer(String id, IModel model) {
        super(id, model);
    }

    @Override
    public boolean isTransparentResolver() {
        return true;
    }
}
