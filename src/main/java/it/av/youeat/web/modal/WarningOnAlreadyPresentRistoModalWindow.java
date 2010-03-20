package it.av.youeat.web.modal;

import it.av.youeat.web.page.RistoranteAddNewPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public final class WarningOnAlreadyPresentRistoModalWindow extends ModalWindow {

    public WarningOnAlreadyPresentRistoModalWindow(String id, final RistoranteAddNewPage addNewPage) {
        super(id);
        setInitialHeight(300);
        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setCookieName("youeat-WarningOnAlreadyPresentRistoModalWindow");
        setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                addNewPage.setPersistTheRisto(true);
                return true;
            }
        });
    }

}
