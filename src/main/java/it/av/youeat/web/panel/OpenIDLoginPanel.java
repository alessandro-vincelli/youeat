package it.av.youeat.web.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenIDLoginPanel extends Panel {

    public OpenIDLoginPanel(String id) {
        super(id);
    }

    /**
     * This method will the panel
     */
    public void createPanel() {

    }

    /**
     * Do your own kind of auth check
     * 
     * @return
     */
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

}
