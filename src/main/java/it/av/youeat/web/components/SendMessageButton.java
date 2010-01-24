package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.Model;

/**
 * Sends message to a user
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public final class SendMessageButton extends AjaxFallbackLink<EaterRelation> {
    private final Eater recipient;
    private final Eater sender;
    private final ModalWindow sendMessageMW;
    private static final long serialVersionUID = 1L;

    public SendMessageButton(String id, Eater sender, Eater recipient, EaterRelation relation, ModalWindow sendMessageMW) {
        super(id);
        this.recipient = recipient;
        this.sender = sender;
        this.sendMessageMW = sendMessageMW;
        if (relation != null) {
            this.setVisible(relation.isActiveFriendRelation());
        } else {
            this.setVisible(false);
        }
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        sendMessageMW.setContent(new SendMessagePanel(sendMessageMW.getContentId(), sendMessageMW, sender, recipient));
        sendMessageMW.setTitle(getString("mw.sendMessageTitle", new Model<Eater>(recipient)));
        sendMessageMW.show(target);
    }
}
