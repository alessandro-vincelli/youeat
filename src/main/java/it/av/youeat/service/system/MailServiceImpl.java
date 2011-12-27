/**
 * 
 */
package it.av.youeat.service.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.support.PrepareMessage;
import it.av.youeat.web.Locales;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.wicket.markup.html.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SimpleMailMessage notificationTemplateMessage;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PrepareMessage prepareMessage;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageReceivedNotification(Eater eater, Message message, WebPage page) {
        Locale locale = Locales.getSupportedLocale(eater.getLanguage().getLanguage());
        Object[] params = { message.getSender().getFirstname() + " " + message.getSender().getLastname() };
        String subject = messageSource.getMessage("notification.newmessage.mailSubject", params, locale);
        String body = prepareMessage.mailTextNotifyNewMessage(eater, message, locale, page);
        sendNotificationMail(subject, body, eater.getEmail());
    }

    /**
     * Send a notification mail to the given recipient mail address
     * 
     * @param subject subject of the e-mail
     * @param message message to notify
     * @param recipient a valid email address
     */
    private void sendNotificationMail(String subject, String message, String recipient) {
        SimpleMailMessage m = new SimpleMailMessage(notificationTemplateMessage);
        m.setTo(recipient);
        m.setSubject(subject);
        m.setText(message);
        m.setSentDate(new Date(System.currentTimeMillis()));
        javaMailSender.send(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPassword(Eater eater, String newPassword) {
        Locale locale = Locales.getSupportedLocale(eater.getLanguage().getLanguage());
        String subject = (messageSource.getMessage("pwdRecover.message.subject", null, locale));
        String message = prepareMessage.mailTextPasswordRecover(eater, newPassword, locale);
        sendNotificationMail(subject, message, eater.getEmail());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendFriendSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient, WebPage page) {
        String subject = prepareMessage.titleForSuggestionNotification(sender, friendsToSuggest, recipient);
        String body = prepareMessage.mailTextSuggestionNotification(sender, friendsToSuggest, recipient, page);
        sendNotificationMail(subject, body, recipient.getEmail());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendFriendRequestNotification(Eater sender, Eater recipient) {
        String subject = prepareMessage.messageTitleForFriendRequestNotification(sender, recipient);
        String body = prepareMessage.messageBodyForFriendRequestNotification(sender, recipient);
        sendNotificationMail(subject, body, recipient.getEmail());
    }
}