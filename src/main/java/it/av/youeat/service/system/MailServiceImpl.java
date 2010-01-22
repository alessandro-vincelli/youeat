/**
 * 
 */
package it.av.youeat.service.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.web.Locales;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void SendMessageReceivedNotification(Eater eater, Message message) {
        Locale locale = Locales.getSupportedLocale(eater.getLanguage().getLanguage());
        Object[] params = {eater.getFirstname() + " " + eater.getLastname() };
        String subject = messageSource.getMessage("notification.newmessage.mailSubject", params, locale);
        String body = prepareMailTextNotifyNewMessage(eater, message, locale);
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

    private String prepareMailTextNotifyNewMessage(Eater eater, Message message, Locale locale) {
        StringBuffer textBody = new StringBuffer();
        textBody.append("\n\n");
        String[] params = { eater.getFirstname() };
        textBody.append(messageSource.getMessage("notification.newmessage.startMailBody", params, locale));
        textBody.append("\n\n");
        if(StringUtils.isNotBlank(message.getTitle())){
            textBody.append(message.getTitle());
            textBody.append("\n\n");    
        }
        textBody.append(message.getBody());
        textBody.append("\n\n");
        textBody.append("http://www.youeat.org");
        textBody.append("\n");
        return textBody.toString();
    }
}