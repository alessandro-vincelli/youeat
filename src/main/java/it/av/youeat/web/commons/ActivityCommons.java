package it.av.youeat.web.commons;

import it.av.youeat.ocm.model.ActivityRistorante;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;


public final class ActivityCommons {

    private ActivityCommons() {
    };

    public static final Image createActivityIcon(Class pageClass, final ListItem<ActivityRistorante> item) {
        // default activity icon is plus
        
        ResourceReference img = new PackageResourceReference(pageClass, "resources/images/plus_64.png");
        if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_MODIFICATION)) {
            img = new PackageResourceReference(pageClass, "resources/images/pencil_64.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_VOTED)) {
            img = new PackageResourceReference(pageClass, "resources/images/voted.gif");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_NEW_COMMENT)) {
            img = new PackageResourceReference(pageClass, "resources/images/comment.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_TRIED)) {
            img = new PackageResourceReference(pageClass, "resources/images/tick_64.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_ADDED_AS_FAVOURITE)) {
            img = new PackageResourceReference(pageClass, "resources/images/clip.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_REMOVED_AS_FAVOURITE)) {
            img = new PackageResourceReference(pageClass, "resources/images/removed-clip.png");
        }
        
        return new Image("type", new Model<ResourceReference>(img)) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.getAttributes().put("alt", item.getModelObject().getType());
                tag.getAttributes().put("title", item.getModelObject().getType());
            }
        };
    }

}
