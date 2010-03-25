/**
 * 
 */
package it.av.youeat.service.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.util.TemplateUtil;
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
    @Autowired
    private TemplateUtil templateUtil;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageReceivedNotification(Eater eater, Message message) {
        Locale locale = Locales.getSupportedLocale(eater.getLanguage().getLanguage());
        Object[] params = { message.getSender().getFirstname() + " " + message.getSender().getLastname() };
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
        String[] params = { message.getSender().getFirstname() };
        textBody.append(messageSource.getMessage("notification.newmessage.startMailBody", params, locale));
        textBody.append("\n\n");
        if (StringUtils.isNotBlank(message.getTitle())) {
            textBody.append(message.getTitle());
            textBody.append("\n\n");
        }
        String body = templateUtil.resolveTemplateEater(message, true, null);
        textBody.append(body);
        textBody.append("\n\n");
        textBody.append("http://www.youeat.org");
        textBody.append("\n");
        return textBody.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPassword(Eater eater, String newPassword) {
        Locale locale = Locales.getSupportedLocale(eater.getLanguage().getLanguage());
        String subject = (messageSource.getMessage("pwdRecover.message.subject", null, locale));
        String message = prepareMailTextPasswordRecover(eater, newPassword, locale);
        sendNotificationMail(subject, message, eater.getEmail());
    }

    private String prepareMailTextPasswordRecover(Eater eater, String newPassword, Locale locale) {
        StringBuffer textBody = new StringBuffer();
        textBody.append("\n\n");
        String[] params = { eater.getFirstname() };
        textBody.append(messageSource.getMessage("pwdRecover.message.startMailBody", params, locale));
        textBody.append("\n\n");
        textBody.append(newPassword);
        textBody.append("\n\n");
        textBody.append("http://www.youeat.org");
        textBody.append("\n");
        return textBody.toString();
    }

}