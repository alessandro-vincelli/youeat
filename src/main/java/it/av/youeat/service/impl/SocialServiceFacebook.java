package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.SocialService;
import it.av.youeat.service.support.PrepareMessage;
import it.av.youeat.web.Locales;
import it.av.youeat.web.security.FacebookAuthenticationProvider;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;

/**
 * FaceBook social networks services
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Repository
@Transactional(readOnly = true)
public class SocialServiceFacebook implements SocialService {

    private static Logger log = LoggerFactory.getLogger(FacebookAuthenticationProvider.class);
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PrepareMessage prepareMessage;
    private String apiKey;
    private String secret;
    private String applicationID;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageReceivedNotification(Eater recipient, Message message) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        String messageBody = prepareMessage.mailTextNotifyNewMessage(recipient, message, locale);
        Object[] params = { message.getSender().getFirstname() + " " + message.getSender().getLastname() };
        String subject = messageSource.getMessage("notification.newmessage.mailSubject", params, locale);
        sendTextEmail(recipient, subject, messageBody);
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
            client.stream_publish(prepareActivityMessage(activityRistorante), null, null, Long.valueOf(applicationID), uid);
        } catch (FacebookException e) {
            log.error("Error sending notification througth facebook", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void sendFriendSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient) {
        String subject = prepareMessage.titleForSuggestionNotification(sender, friendsToSuggest, recipient);
        String body = prepareMessage.mailTextSuggestionNotification(sender, friendsToSuggest, recipient);
        sendTextEmail(recipient, subject, body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void sendFriendRequestNotification(Eater fromUser, Eater toUser) {
        String subject = prepareMessage.messageTitleForFriendRequestNotification(fromUser, toUser);
        String body = prepareMessage.messageBodyForFriendRequestNotification(fromUser, toUser);
        sendTextEmail(toUser, subject, body);
    }

    /**
     * @param recipient
     * @param subject
     * @param body
     */
    private void sendTextEmail(Eater recipient, String subject, String body) {
        try {
            FacebookJaxbRestClient client = new FacebookJaxbRestClient(apiKey, secret);
            ArrayList<Long> recipients = new ArrayList<Long>(1);
            if (recipient.isSocialNetworkEater()) {
                recipients.add(Long.valueOf(recipient.getSocialUID()));
                client.notifications_sendTextEmail(recipients, subject, body);
            }
        } catch (FacebookException e) {
            log.error("Error sending notification by facebook", e);
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