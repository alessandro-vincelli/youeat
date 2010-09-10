package it.av.youeat.web.page.manager;

import it.av.youeat.ocm.model.Comment;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Provides the hide {@link Link} to open the Comment and shows some infos on the comment
 * 
 * @author Alessandro Vincelli
 * 
 */
public class CommentNameColumn extends Panel {

    /**
     * @param id component id
     * @param model model for Comment
     */
    public CommentNameColumn(String id, final IModel<Comment> model) {
        super(id, model);
        Link<Comment> link = new Link<Comment>("link", model) {

            @Override
            public void onClick() {
                
            }
        };
        link.add(new Label("title",  StringUtils.abbreviate(model.getObject().getBody(), 100)));
        add(link);
    }

}