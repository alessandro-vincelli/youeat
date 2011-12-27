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
import org.apache.wicket.injection.Injector;
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
    private int size;
    // pattern text to search
    private String pattern;

    /**
     * Constructor
     */
    public RistoranteSortableDataProvider() {
        super();
        Injector.get().inject(this);
        results = new ArrayList<Ristorante>(0);
        size = 0;
        // setSort(LightVac.SortedFieldNames.dateTime.value(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<Ristorante> iterator(int first, int count) {
        results = ristoranteService.freeTextSearch(this.pattern, first, count);
        return Collections.synchronizedList(new ArrayList<Ristorante>(results)).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size() {
        if (size == 0 && StringUtils.isNotBlank(pattern)) {
            size = ristoranteService.countfreeTextSearch(this.pattern, null);
            return size;
        } else {
            return size;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IModel<Ristorante> model(Ristorante ristorante) {
        return new RistoranteDetachableModel(ristorante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {
        results = null;
    }

    /**
     * Performs the search
     * 
     * @param pattern the string to use in the search
     * @throws YoueatException
     */
    public final void fetchResults(String pattern, int maxResultXPage) throws YoueatException {
        if (StringUtils.isNotBlank(pattern)) {
            this.pattern = LuceneUtil.removeSpecialChars(pattern);
            //results = ristoranteService.freeTextSearch(this.pattern, 0, maxResultXPage);
        }
    }

}