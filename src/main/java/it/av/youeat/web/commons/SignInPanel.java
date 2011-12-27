package it.av.youeat.web.commons;

public class SignInPanel extends org.apache.wicket.authroles.authentication.panel.SignInPanel {

    public SignInPanel(String id, boolean includeRememberMe) {
        super(id, includeRememberMe);
        remove("feedback");
        add(new YouEatFeedbackPanel("feedback"));
    }

}
