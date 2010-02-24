package it.av.youeat.web.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

public final class SendMessageModalWindow {

    private SendMessageModalWindow() {
    }

    public static ModalWindow getNewModalWindow(String id) {
        ModalWindow sendMessageMW = new ModalWindow(id);
        sendMessageMW.setInitialHeight(200);
        sendMessageMW.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        sendMessageMW.setCookieName("youeat-sendMessageModalWindow");

        sendMessageMW.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                // setResult("Modal window 2 - close button");
                return true;
            }
        });

        sendMessageMW.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
            }
        });
        return sendMessageMW;
    }

}
