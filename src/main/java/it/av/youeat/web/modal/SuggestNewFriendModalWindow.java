package it.av.youeat.web.modal;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * Create a modal window to show a list of friends to suggest to another friend
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public final class SuggestNewFriendModalWindow extends ModalWindow {

    /**
     * Constructor, initialize some settings
     * 
     * @param id
     */
    public SuggestNewFriendModalWindow(String id) {
        super(id);
        setHeightUnit("%");
        setInitialHeight(70);
        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setCookieName("youeat-SuggestNewFriendModalWindow");
    }
}