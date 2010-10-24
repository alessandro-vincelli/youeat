/**
 * 
 */
package it.av.youeat.web.commons;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * FeedPanel that specialize some graphic beahavior
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class YouEatFeedbackPanel extends FeedbackPanel {

    /**
     * {@inheritDoc}
     */
    public YouEatFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    /**
     * {@inheritDoc}
     */
    public YouEatFeedbackPanel(String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCSSClass(FeedbackMessage message) {
        if(message.getLevel() <= 400){
            return "ui-state-highlight ui-corner-all";    
        }
        else{
            return "ui-state-error ui-corner-all";
        }
    }
}