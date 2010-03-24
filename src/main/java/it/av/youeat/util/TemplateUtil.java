package it.av.youeat.util;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.EaterService;
import it.av.youeat.web.url.YouetGeneratorURL;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Templates are text blocks that can be dynamically loaded inside another page whenever that page is requested.
 * <p>
 * The template is a special link in double curly brackets
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class TemplateUtil {
    public static final String OPEN_EATER_DOUBLE_CURLYBRACKETS = "{{user ";
    public static final String CLOSE_DOUBLE_CURLYBRACKETS = "}}";
    public static final String USER = "user";
    @Autowired
    EaterService eaterService;
    @Autowired
    YouetGeneratorURL youetGeneratorURL;

    /**
     * Generate a template for an eater using the eater id, like: {{user ID}}
     * 
     * @param eater
     * @return templated user
     */
    public static String templateEater(Eater eater) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(OPEN_EATER_DOUBLE_CURLYBRACKETS);
        buffer.append(eater.getId());
        buffer.append(CLOSE_DOUBLE_CURLYBRACKETS);
        return buffer.toString();
    }

    /**
     * Resolve eater templates the given message
     * <p>
     * Resolves the {{user ID}} in: Firstname Lastname
     * <p>
     * If <b>generateLinks</b> is true add a link <a href="...">Firstname Lastname</a>
     * 
     * @param message message to parse
     * @param generateLinks creates links on found eater
     * @param className class name for a custom CSS style (optional)
     * @return resolved template string message
     */
    public String resolveTemplateEater(Message message, boolean generateLinks, String className) {
        String text = message.getBody();
        String[] templatesID = StringUtils.substringsBetween(text, OPEN_EATER_DOUBLE_CURLYBRACKETS, CLOSE_DOUBLE_CURLYBRACKETS);
        if (templatesID == null) {
            return text;
        }
        for (String id : templatesID) {
            Eater eater = eaterService.getByID(StringUtils.trim(id));
            text = StringUtils.replace(text, OPEN_EATER_DOUBLE_CURLYBRACKETS + id + CLOSE_DOUBLE_CURLYBRACKETS,
                    extractNameAndUrls(eater, generateLinks, className));
        }
        return text;
    }

    /**
     * Extracts eater name and if requested surround the name with a link tag
     * <ul>
     * <li>
     * without link: Firstname Lastname
     * <li>
     * with links:<a href="...">Firstname Lastname</a>
     * 
     * @param eater
     * @param generateLink true to add the link to the user
     * @param className class name for a custom CSS style (optional) 
     * @return eater name, with link if requested
     */
    String extractNameAndUrls(Eater eater, boolean generateLink, String className) {
        StringBuffer buffer = new StringBuffer();
        if (generateLink) {
            String url = youetGeneratorURL.getEaterUrl(eater);
            buffer.append("<a href=\"");
            buffer.append(url);
            buffer.append("\"");
            if(className != null){
                buffer.append(" class=\"");
                buffer.append(className);
                buffer.append("\"");
            }
            buffer.append(">");
            buffer.append(eater.getFirstname());
            buffer.append(" ");
            buffer.append(eater.getLastname());
            buffer.append("</a>");
            return buffer.toString();
        } else {
            buffer.append(eater.getFirstname());
            buffer.append(" ");
            buffer.append(eater.getLastname());
            return buffer.toString();
        }
    }
}