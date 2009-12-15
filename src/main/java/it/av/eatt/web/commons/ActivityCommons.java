package it.av.eatt.web.commons;

import it.av.eatt.ocm.model.ActivityRistorante;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;

public class ActivityCommons {
    public static final Image createActivityIcon(Class pageClass, final ListItem<ActivityRistorante> item) {
        // default activity icon is plus
        ResourceReference img = new ResourceReference(pageClass, "resources/images/plus_64.png");
        if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_MODIFICATION)) {
            img = new ResourceReference(pageClass, "resources/images/pencil_64.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_VOTED)) {
            img = new ResourceReference(pageClass, "resources/images/voted.gif");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_TRIED)) {
            img = new ResourceReference(pageClass, "resources/images/tick_64.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_ADDED_AS_FAVOURITE)) {
            img = new ResourceReference(pageClass, "resources/images/clip.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_REMOVED_AS_FAVOURITE)) {
            img = new ResourceReference(pageClass, "resources/images/removed-clip.png");
        }

        return new Image("type", img) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.getAttributes().put("alt", item.getModelObject().getType());
                tag.getAttributes().put("title", item.getModelObject().getType());
            }
        };
    }

    

}
