package it.av.youeat.web.page;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistoranteRevision;
import it.av.youeat.web.util.TextDiffRender;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * The panel displays the revisions form
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteRevisionsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private PropertyListView<RistoranteRevision> productsVersionsList;
    private List<RistoranteRevision> revisions = new ArrayList<RistoranteRevision>();
    final FeedbackPanel feedbackPanel;

    /**
     * Constructor
     * 
     * @param revisions
     * @param id
     * @param feedbackPanel
     */
    public RistoranteRevisionsPanel(String id, final FeedbackPanel feedbackPanel) {
        super(id);
        this.feedbackPanel = feedbackPanel;
        productsVersionsList = new PropertyListView<RistoranteRevision>("versions", revisions) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<RistoranteRevision> item) {
                item.add(new Label("revisionNumber"));
                item.add(new Label("modificationTime"));
                item.add(new Label("name").setEscapeModelStrings(false));
                item.add(new Label("address").setEscapeModelStrings(false));
            }
        };
        add(productsVersionsList.setOutputMarkupId(true));

    }

    /**
     * Fill with fresh data the repeater
     * 
     * @throws YoueatException
     */
    public void refreshRevisionsList(Ristorante ristoSelected) {
        try {
            if (ristoSelected != null) {
                // revisions = RistoranteRevisionUtil.cloneList(ristoSelected.getRevisions());
                revisions = ristoSelected.getRevisions();
                if (revisions.size() > 1) {
                    // Latest two releases
                    RistoranteRevision r1 = revisions.get(revisions.size() - 1);
                    RistoranteRevision r2 = revisions.get(revisions.size() - 2);
                    performDiff(r2, r1);
                }

                productsVersionsList.setModelObject(revisions);
            } else {
                productsVersionsList.setModelObject(revisions);
            }
        } catch (YoueatException e) {
            feedbackPanel.error(e.getMessage());
        }
    }

    public final PropertyListView<RistoranteRevision> getProductsVersionsList() {
        return productsVersionsList;
    }

    public final void setProductsVersionsList(PropertyListView<RistoranteRevision> productsVersionsList) {
        this.productsVersionsList = productsVersionsList;
    }

    private void performDiff(RistoranteRevision ori, RistoranteRevision newVer) throws YoueatException {
        TextDiffRender diffRender = new TextDiffRender();

        // description is deprecated, now it must be supported multilanguage descriptions
        // String[] diff = diffRender.render(ori.getDescription(), newVer.getDescription());
        // ori.setDescription(diff[0]);
        // newVer.setDescription(diff[1]);

        String[] diff = diffRender.render(ori.getName(), newVer.getName());
        ori.setName(diff[0]);
        newVer.setName(diff[1]);

        diff = diffRender.render(ori.getAddress(), newVer.getAddress());
        ori.setAddress(diff[0]);
        newVer.setAddress(diff[1]);
    }
}
