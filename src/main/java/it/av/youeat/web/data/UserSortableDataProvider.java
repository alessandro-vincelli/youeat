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

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.EaterService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class UserSortableDataProvider extends SortableDataProvider<Eater> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterService usersService;
    private transient Collection<Eater> results;
    private boolean attached;

    /**
     * Construct
     */
    public UserSortableDataProvider() {
        super();
        results = new ArrayList<Eater>();
        // setSort(LightVac.SortedFieldNames.dateTime.value(), true);
        InjectorHolder.getInjector().inject(this);
        attached = true;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final Iterator<Eater> iterator(int first, int count) {
        return Collections.synchronizedList(new ArrayList<Eater>(results)).subList(first, first + count).iterator();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final int size() {
        return results.size();
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

    /**
     * Performs the search
     * 
     * @param searchData the string to use for the search
     * @throws YoueatException
     */
    public final void fetchResults(String searchData) throws YoueatException {
        if (StringUtils.isNotBlank(searchData)) {
            results = usersService.find(searchData);
        } else {
            results = usersService.getAll();
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (attached) {
            attached = false;
            results = null;
        }
    }

}