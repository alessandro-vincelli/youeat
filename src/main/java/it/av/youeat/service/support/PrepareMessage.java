/**
 * 
 */
package it.av.youeat.service.support;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.util.TemplateUtil;
import it.av.youeat.web.Locales;
import it.av.youeat.web.url.YouetGeneratorURL;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Utility class to create e-mail message
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class PrepareMessage {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private TemplateUtil templateUtil;
    @Autowired
    private YouetGeneratorURL generatorURL;

    public String mailTextNotifyNewMessage(Eater eater, Message message, Locale locale) {
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

    public String mailTextPasswordRecover(Eater eater, String newPassword, Locale locale) {
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

    public String mailTextSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        if (friendsToSuggest.size() == 1) {
            Eater eaterToSuggest = friendsToSuggest.iterator().next();
            StringBuffer textBody = new StringBuffer();
            // params [1=sender], [2=newFriend]
            Object[] paramsBody = { sender, eaterToSuggest };
            textBody.append(messageSource.getMessage("mail.suggestNewFriend.body", paramsBody, locale));
            textBody.append("\n");
            textBody.append(generatorURL.getEaterUrl(eaterToSuggest));
            textBody.append("\n\n");
            textBody.append("http://www.youeat.org");
            textBody.append("\n");
            return textBody.toString();
        } else {
            // params [1=sender]
            StringBuffer friendsList = new StringBuffer();
            for (Iterator<Eater> iterator = friendsToSuggest.iterator(); iterator.hasNext();) {
                Eater eaterToSuggest = (Eater) iterator.next();
                friendsList.append(eaterToSuggest);
                friendsList.append("\n");
                friendsList.append(generatorURL.getEaterUrl(eaterToSuggest));
                if (iterator.hasNext()) {
                    friendsList.append("\n");
                    friendsList.append("\n");
                }
            }
            StringBuffer textBody = new StringBuffer();
            // params [1=sender], [2=newFriendListCommaSepared]
            Object[] params2 = { sender.toString(), friendsList.toString() };
            textBody.append(messageSource.getMessage("mail.suggestNewFriend.bodyMultipleUsers", params2, locale));
            textBody.append("\n\n");
            textBody.append("http://www.youeat.org");
            textBody.append("\n");
            return textBody.toString();
        }
    }

    public String titleForSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        if (friendsToSuggest.size() == 1) {
            // params [1=sender]
            Object[] params = { sender.getFirstname() };
            return messageSource.getMessage("mail.suggestNewFriend.title", params, locale);
        } else {
            // params [1=sender]
            Object[] params = { sender.getFirstname() };
            return messageSource.getMessage("mail.suggestNewFriend.titleMultipleUsers", params, locale);
        }
    }

    public String messageBodyForFriendRequestNotification(Eater sender, Eater recipient) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        StringBuffer message = new StringBuffer();
        Object[] params = { recipient.getFirstname(), sender };
        message.append(messageSource.getMessage("mail.friendRequest.body", params, locale));
        message.append(messageSource.getMessage("mail.end.body", null, locale));
        return message.toString();
    }

    public String messageTitleForFriendRequestNotification(Eater sender, Eater recipient) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        StringBuffer message = new StringBuffer();
        Object[] params = { sender };
        message.append(messageSource.getMessage("mail.friendRequest.title", params, locale));
        return message.toString();
    }
}
