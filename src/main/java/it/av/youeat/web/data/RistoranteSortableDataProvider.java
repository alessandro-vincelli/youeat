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
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.util.LuceneUtil;

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
 * 
 */
public class RistoranteSortableDataProvider extends SortableDataProvider<Ristorante> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private RistoranteService ristoranteService;
    private transient Collection<Ristorante> results;
    private boolean attached;

    /**
     * 
     * @param ristoranteService
     */
    public RistoranteSortableDataProvider() {
        super();
        InjectorHolder.getInjector().inject(this);
        results = new ArrayList<Ristorante>(0);
        attached = true;
        // setSort(LightVac.SortedFieldNames.dateTime.value(), true);
    }

    /**
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int, int)
     */
    @Override
    public final Iterator<Ristorante> iterator(int first, int count) {
        return Collections.synchronizedList(new ArrayList<Ristorante>(results)).subList(first, first + count)
                .iterator();
    }

    /**
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public final int size() {
        if(results != null){
            return results.size();    
        }
        else{
            return 0;
        }
    }

    /**
     * @param ristorante
     * @return IModel<ristorante>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
     */
    @Override
    public final IModel<Ristorante> model(Ristorante ristorante) {
        return new RistoranteDetachableModel(ristorante);
    }

    /**
     * @see org.apache.wicket.model.IDetachable#detach()
     */
    @Override
    public void detach() {
        if (attached) {
            attached = false;
            results = new ArrayList<Ristorante>(0);
        }
    }

    /**
     * Performs the search
     * 
     * @param pattern the string to use in the search
     * @throws YoueatException
     */
    public final void fetchResults(String pattern) throws YoueatException {
        if (StringUtils.isNotBlank(pattern)) {
            results = ristoranteService.freeTextSearch(LuceneUtil.removeSpecialChars(pattern));
        }
    }

}