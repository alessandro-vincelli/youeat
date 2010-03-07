/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.web.page.manager;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.components.RistoNameColumn;
import it.av.youeat.web.components.RistoranteDataTable;
import it.av.youeat.web.data.RistoranteSortableDataProvider;
import it.av.youeat.web.page.BasePage;
import it.av.youeat.web.panel.RistoranteSearchPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Ristorante search and admin page
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "ADMIN" })
public class RistoranteManagerPage extends BasePage {

    public RistoranteManagerPage() {
        RistoranteSortableDataProvider ristoranteSortableDataProvider = new RistoranteSortableDataProvider();
        List<IColumn<Ristorante>> columns = new ArrayList<IColumn<Ristorante>>();
        columns.add(new AbstractColumn<Ristorante>(new Model<String>("")) {
            public void populateItem(Item<ICellPopulator<Ristorante>> cellItem, String componentId,
                    IModel<Ristorante> model) {
                cellItem.add(new RistoNameColumn(componentId, model));
            }

            @Override
            public String getCssClass() {
                return "ristoName";
            }

            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });

        columns.add(new PropertyColumn<Ristorante>(new Model<String>(new StringResourceModel("city", this, null)
                .getString()), "city") {
            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });

        columns.add(new AbstractColumn<Ristorante>(new Model<String>(new StringResourceModel(
                "datatableactionpanel.actions", this, null).getString())) {
            public void populateItem(Item<ICellPopulator<Ristorante>> cellItem, String componentId,
                    IModel<Ristorante> model) {
                cellItem.add(new RistoActionColumn(componentId, model));
            }

            @Override
            public String getCssClass() {
                return "ristoName";
            }

            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });

        RistoranteDataTable<Ristorante> ristoranteDataTable = new RistoranteDataTable<Ristorante>(
                "ristoranteDataTable", columns, ristoranteSortableDataProvider, 10);
        add(ristoranteDataTable);

        RistoranteSearchPanel ristoranteSearchPanel = new RistoranteSearchPanel("ristoranteSearchPanel",
                ristoranteSortableDataProvider, ristoranteDataTable, getFeedbackPanel());
        add(ristoranteSearchPanel);

    }

}