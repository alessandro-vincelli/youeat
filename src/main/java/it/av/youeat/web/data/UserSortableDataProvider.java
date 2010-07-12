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
package it.av.youeat.web.data;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.EaterService;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class UserSortableDataProvider extends SortableDataProvider<Eater> {
    @SpringBean
    private EaterService usersService;
    private String searchData = ""; 

    /**
     * Construct
     */
    public UserSortableDataProvider() {
        super();
        setSort(Eater.ID, true);
        InjectorHolder.getInjector().inject(this);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final Iterator<Eater> iterator(int first, int count) {
        return usersService.find(searchData,first, count, getSort().getProperty(), getSort().isAscending()).iterator();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final int size() {
        return usersService.count(searchData);
    }

    /**
     * @param user
     * @return IModel<Eater>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
     */
    @Override
    public final IModel<Eater> model(Eater user) {
        return new UserDetachableModel(user);
    }

    @Override
    public void detach() {
        super.detach();
    }

    public String getSearchData() {
        return searchData;
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }

}