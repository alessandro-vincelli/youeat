package it.av.youeat.web.page.manager;

import it.av.youeat.ocm.model.Comment;
import it.av.youeat.service.CommentService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Provides some actions to manage the current comment
 * 
 * @author Alessandro Vincelli
 * 
 */
public class CommentActionColumn extends Panel {
    @SpringBean
    private CommentService commentService;

    /**
     * @param id component id
     * @param model model for comment
     */
    public CommentActionColumn(String id, final IModel<Comment> model) {
        super(id, model);
        InjectorHolder.getInjector().inject(this);
        Link<Comment> link = new Link<Comment>("remove", model) {

            @Override
            public void onClick() {
                commentService.disable(model.getObject());
                setResponsePage(getApplication().getHomePage());
                setRedirect(true);
            }
        };
        add(link);
    }

}