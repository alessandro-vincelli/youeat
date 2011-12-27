package it.av.youeat.web.components;

import javax.management.RuntimeErrorException;

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
 * @deprecated
 */
public class TransparentWebMarkupContainer extends WebMarkupContainer {

    public TransparentWebMarkupContainer(String id) {
        super(id);
        throw new RuntimeErrorException(null);
    }

    public TransparentWebMarkupContainer(String id, IModel model) {
        super(id, model);
        throw new RuntimeErrorException(null);
    }

//   TODO 1.5 @Override
//    public boolean isTransparentResolver() {
//        return true;
//    }
}
