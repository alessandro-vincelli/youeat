package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.SocialService;
import it.av.youeat.web.Locales;
import it.av.youeat.web.security.FacebookAuthenticationProvider;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;

@Repository
@Transactional(readOnly = true)
public class SocialServiceFacebook implements SocialService {

    private static Logger log = LoggerFactory.getLogger(FacebookAuthenticationProvider.class);
    @Autowired
    private MessageSource messageSource;
    private String apiKey;
    private String secret;
    private String applicationID;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageReceivedNotification(Eater recipient, Message message) {
//        if (StringUtils.isBlank(sender.getSocialSessionKey())) {
//            throw new YoueatException("Social Session key not present");
//        }
//
//        FacebookJaxbRestClient client = new FacebookJaxbRestClient(apiKey, secret);
//        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
//        String messageBody = prepareMailTextNotifyNewMessage(recipient, message, locale);
//        Object[] params = { message.getSender().getFirstname() + " " + message.getSender().getLastname() };
//        String subject = messageSource.getMessage("notification.newmessage.mailSubject", params, locale);
//        client.setCacheSession(sender.getSocialSessionKey(), null, Long.valueOf("1000000"));
//        // client.notifications_sendFbmlEmailToCurrentUser(subject, messageBody);
//        ArrayList<Long> recipients = new ArrayList<Long>(1);
//        // recipients.add(Long.valueOf(recipient.getSocialUID()));
//        // //Collection<String> results = client.notifications_sendTextEmail(recipients, subject, messageBody);
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
        textBody.append(message.getBody());
        textBody.append("\n\n");
        textBody.append("http://www.youeat.org");
        textBody.append("\n");
        return textBody.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void publishRistoActivity(ActivityRistorante activityRistorante) {
        try {
            Long uid = null;
            FacebookJaxbRestClient client = new FacebookJaxbRestClient(apiKey, secret);
            if (activityRistorante.getEater().isSocialNetworkEater()) {
                uid = Long.valueOf(activityRistorante.getEater().getSocialUID());
            }
            client.stream_publish(prepareActivityMessage(activityRistorante), null, null, Long.valueOf(applicationID),
                    uid);
        } catch (FacebookException e) {
            log.error("Error sending notification througth facebook", e);
        }
    }

    private String prepareActivityMessage(ActivityRistorante activityRistorante) {
        Locale locale = Locales.ENGLISH;
        StringBuffer textBody = new StringBuffer();
        textBody.append("\n\n");
        textBody.append(activityRistorante.getEater());
        textBody.append(" ");
        textBody.append(messageSource.getMessage(activityRistorante.getType(), null, locale));
        textBody.append(" ");
        textBody.append(activityRistorante.getRistorante().getName());
        textBody.append("\n\n");
        textBody.append("http://www.youeat.org");
        textBody.append("\n");
        return textBody.toString();
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }
}