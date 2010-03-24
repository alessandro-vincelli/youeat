package it.av.youeat.web.panel;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.DialogService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.web.components.ImagesAvatar;
import it.av.youeat.web.modal.SuggestNewFriendModalWindow;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The panel displays friends of the user to send friend suggestion
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SuggestNewFriendsPanel extends Panel {

    @SpringBean
    private EaterRelationService eaterRelationService;
    @SpringBean
    private DialogService dialogService;
    private Set<Eater> selectedEater;

    /**
     * Constructor
     * 
     * @param id
     * @param sender user that sends the suggestion
     * @param recipient recipient of the suggestion
     * @param modalWindow modalWindow parent of this panel
     */
    public SuggestNewFriendsPanel(String id, final Eater sender, final Eater recipient,
            final SuggestNewFriendModalWindow modalWindow) {
        super(id);
        selectedEater = new HashSet<Eater>();
        InjectorHolder.getInjector().inject(this);
        add(new Label("title", getString("suggestNewFriendPanel.title", new Model<Eater>(recipient))));
        Form form = new Form("form");
        add(form);
        form.add(new SubmitButton("submit", form, modalWindow, sender, recipient));
        form.add(new SubmitButton("submitBottom", form, modalWindow, sender, recipient));
        PropertyListView<Eater> friendsListView = new PropertyListView<Eater>("friendsList", eaterRelationService
                .getNonCommonFriends(sender, recipient)) {
            @Override
            protected void populateItem(final ListItem<Eater> item) {
                item.add(ImagesAvatar.getAvatar("avatar", item.getModelObject(), this.getPage(), true));
                item.add(new Label("firstname").setEscapeModelStrings(false));
                item.add(new Label("lastname").setEscapeModelStrings(false));
                item.add(new AjaxCheckBox("select", new Model<Boolean>(Boolean.FALSE)) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if (getModelObject().booleanValue()) {
                            selectedEater.add(item.getModelObject());
                        } else {
                            selectedEater.remove(item.getModelObject());
                        }
                    }
                });
            }
        };
        form.add(friendsListView.setOutputMarkupId(true));
    }
    
    private final class SubmitButton extends AjaxButton {
        private final SuggestNewFriendModalWindow modalWindow;
        private final Eater sender;
        private final Eater recipient;

        private SubmitButton(String id, Form<?> form, SuggestNewFriendModalWindow modalWindow, Eater sender, Eater recipient) {
            super(id, form);
            this.modalWindow = modalWindow;
            this.sender = sender;
            this.recipient = recipient;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            dialogService.sendFriendSuggestions(sender, recipient, selectedEater);
            modalWindow.close(target);
        }
    }

}