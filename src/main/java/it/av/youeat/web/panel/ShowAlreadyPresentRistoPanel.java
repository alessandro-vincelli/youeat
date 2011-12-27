package it.av.youeat.web.panel;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteAddNewPage;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.springframework.util.Assert;

/**
 * Show a list of ristoranti, used as warning panel to notice the user that the risto that is trying to insert could be already
 * present in youeat
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ShowAlreadyPresentRistoPanel extends Panel {

    /**
     * 
     * @param id
     * @param addNewPage the parent page
     * @param container (not null)
     * @param ristoToSave risto to save
     * @param ristos the list of possible conflicting ristos to show (not null)
     */
    public ShowAlreadyPresentRistoPanel(String id, final RistoranteAddNewPage addNewPage, final ModalWindow container,
            final Ristorante ristoToSave, final List<Ristorante> ristos) {
        super(id);
        Assert.notNull(ristos);
        Assert.notNull(container);
        // Injector.get().inject(this);
        final FeedbackPanel feedbackPanelSMP = new FeedbackPanel("feedbackPanelSMP");
        feedbackPanelSMP.setOutputMarkupId(true);
        feedbackPanelSMP.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanelSMP);
        final AjaxFallbackLink<String> continueAndPersistTheRisto = new AjaxFallbackLink<String>("continueAndPersistTheRisto") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                container.close(target);
                addNewPage.setPersistTheRisto(true);
            }
        };
        continueAndPersistTheRisto.setOutputMarkupId(true);
        continueAndPersistTheRisto.setVisible(true);
        add(continueAndPersistTheRisto);
        PropertyListView<Ristorante> ristoList = new PropertyListView<Ristorante>("ristoList", ristos) {

            @Override
            protected void populateItem(ListItem<Ristorante> item) {
                AjaxFallbackLink<Ristorante> ristoLink = new AjaxFallbackLink<Ristorante>("ristorante.link", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        container.close(target);
                        addNewPage.setPersistTheRisto(false);
                        addNewPage.setRistoranteToRedirect(getModelObject());
                    }
                };
                ristoLink.add(new Label("name", item.getModel().getObject().getName()));
                item.add(ristoLink);
                item.add(new Label("city", item.getModel().getObject().getCity().getName()));
            }
        };
        add(ristoList);
    }
}
