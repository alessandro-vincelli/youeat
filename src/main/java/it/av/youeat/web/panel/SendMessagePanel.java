package it.av.youeat.web.panel;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.DialogService;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.springframework.util.Assert;

/**
 * Contains a form to send internal message to eater 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class SendMessagePanel extends Panel {

    @SpringBean
    private DialogService dialogService;

    /**
     * 
     * @param id
     * @param container (not null)
     * @param sender the sender of the message (not null)
     * @param recipient the recipient to send a message (not null)
     */
    public SendMessagePanel(String id, final ModalWindow container, final Eater sender, final Eater recipient) {
        super(id);
        Assert.notNull(sender);
        Assert.notNull(recipient);
        Assert.notNull(container);
        InjectorHolder.getInjector().inject(this);
        final FeedbackPanel feedbackPanelSMP = new FeedbackPanel("feedbackPanelSMP");
        feedbackPanelSMP.setOutputMarkupId(true);
        feedbackPanelSMP.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanelSMP);
        final AjaxFallbackLink<String> closeAfterSent = new AjaxFallbackLink<String>("closeAfterSent") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                container.close(target);
            }
        };
        closeAfterSent.setOutputMarkupId(true);
        closeAfterSent.setOutputMarkupPlaceholderTag(true);
        closeAfterSent.setVisible(false);
        add(closeAfterSent);
        Message msg = new Message();
        msg.setSender(sender);
        final Form<Message> sendMessageForm = new Form<Message>("sendMessageForm", new CompoundPropertyModel<Message>(
                msg));
        sendMessageForm.setOutputMarkupId(true);
        add(sendMessageForm);
        sendMessageForm
                .add(new TextField<String>("title").add(StringValidator.maximumLength(Message.TITLE_MAX_LENGTH)));
        sendMessageForm.add(new TextArea<String>("body").setRequired(true).add(
                StringValidator.maximumLength(Message.BODY_MAX_LENGTH)));
        sendMessageForm.add(new AjaxButton("submit", sendMessageForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialogService.startNewDialog(sender, recipient, (Message) form.getModelObject());
                feedbackPanelSMP.info(getString("message.messageSent", new Model<Eater>(recipient)));
                target.addComponent(feedbackPanelSMP);
                sendMessageForm.setVisible(false);
                closeAfterSent.setVisible(true);
                target.addComponent(closeAfterSent);
                target.addComponent(sendMessageForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                // I don't want show the error message, layout issues
                // target.addComponent(feedbackPanelSMP);
            }
        });
    }
}
