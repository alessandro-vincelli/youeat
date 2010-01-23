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
package it.av.youeat.web.page;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.data.SearchUserFriendSortableDataProvider;
import it.av.youeat.web.panel.EaterAvatarPanel;
import it.av.youeat.web.panel.SearchFriendPanel;
import it.av.youeat.web.panel.SearchFriendTableActionPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Add and remove friends.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN"})
public class SearchFriendPage extends BasePage {

    private SearchUserFriendSortableDataProvider searchFriendsDataProvider;
    private final WebMarkupContainer searchFriendsContainer;
    private final SearchFriendPanel searchFriendPanel;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public SearchFriendPage() throws YoueatException {
        searchFriendsContainer = new WebMarkupContainer("searchFriendsContainer");
        searchFriendsContainer.setOutputMarkupId(true);
        add(searchFriendsContainer);
        List<IColumn<Eater>> columns = new ArrayList<IColumn<Eater>>();
        columns.add(new AbstractColumn<Eater>(new Model<String>(new StringResourceModel("datatableactionpanel.actions",
                this, null).getString())) {
            public void populateItem(Item<ICellPopulator<Eater>> cellItem, String componentId, IModel<Eater> model) {
                cellItem.add(new SearchFriendTableActionPanel(componentId, model));
            }

            @Override
            public String getCssClass() {
                return "friend-action";
            }

            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("firstname", this, null)
                .getString()), "firstname") {
            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("lastname", this, null)
                .getString()), "lastname") {
            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });
        columns.add(new AbstractColumn<Eater>(null){
            @Override
            public void populateItem(Item<ICellPopulator<Eater>> cellItem, String componentId, IModel<Eater> rowModel) {
                cellItem.add(new EaterAvatarPanel(componentId, rowModel, getPage()));
            }
        });
        searchFriendsDataProvider = new SearchUserFriendSortableDataProvider(getLoggedInUser());
        AjaxFallbackDefaultDataTable<Eater> searchFriendsDataTable = new AjaxFallbackDefaultDataTable<Eater>(
                "searchFriendsDataTable", columns, searchFriendsDataProvider, 20);
        searchFriendsContainer.add(searchFriendsDataTable);
        searchFriendPanel = new SearchFriendPanel(searchFriendsDataProvider, searchFriendsDataTable, "searchPanel",
                getFeedbackPanel(), getLoggedInUser());
        add(searchFriendPanel);
    }

    /**
     * Fill with fresh data the repeater
     * 
     * @throws YoueatException
     */
    public final void refreshDataTable() throws YoueatException {
        String pattern = searchFriendPanel.getSearchBean().getSearchData();
        searchFriendsDataProvider.fetchResults(pattern);
    }

    public WebMarkupContainer getSearchFriendsContainer() {
        return searchFriendsContainer;
    }
}