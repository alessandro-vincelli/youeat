package it.av.youeat.web.panel;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistoranteRevision;
import it.av.youeat.web.util.TextDiffRender;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

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
    private String descriptionOri = "";
    private String descriptionNew = "";

    /**
     * Constructor
     * 
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
                item.add(new MultiLineLabel("descriptionDiff").setEscapeModelStrings(false));
            }
        };
        add(productsVersionsList.setOutputMarkupId(true));

    }

    /**
     * Fill with fresh data the repeater
     * 
     * @throws YoueatException
     */
    public void refreshRevisionsList(final Ristorante ristoSelected, final Language langSelected) {
        if (ristoSelected != null) {
            // revisions = RistoranteRevisionUtil.cloneList(ristoSelected.getRevisions());

            productsVersionsList.setModel(new LoadableDetachableModel<List<RistoranteRevision>>() {

                @Override
                protected List<RistoranteRevision> load() {
                    List<RistoranteRevision> revisions = ristoSelected.getRevisions();
                    if (revisions.size() > 1) {
                        // Latest two releases, reverse order!
                        RistoranteRevision r1 = revisions.get(1);
                        RistoranteRevision r2 = revisions.get(0);
                        performDiff(r1, r2, langSelected);
                    }
                    return revisions;
                }
            });
        }

    }

    public final PropertyListView<RistoranteRevision> getProductsVersionsList() {
        return productsVersionsList;
    }

    public final void setProductsVersionsList(PropertyListView<RistoranteRevision> productsVersionsList) {
        this.productsVersionsList = productsVersionsList;
    }

    private void performDiff(RistoranteRevision ori, RistoranteRevision newVer, Language lang) throws YoueatException {
        TextDiffRender diffRender = new TextDiffRender();

        //description is deprecated, now it must be supported multilanguage descriptions
        String[] diff = diffRender.render(ori.getDesctiptionByLanguage(lang).getDescription(), newVer.getDesctiptionByLanguage(lang).getDescription());
        ori.setDescriptionDiff(diff[0]);
        newVer.setDescriptionDiff(diff[1]);

        diff = diffRender.render(ori.getName(), newVer.getName());
        ori.setName(diff[0]);
        newVer.setName(diff[1]);

        diff = diffRender.render(ori.getAddress(), newVer.getAddress());
        ori.setAddress(diff[0]);
        newVer.setAddress(diff[1]);
    }
}
