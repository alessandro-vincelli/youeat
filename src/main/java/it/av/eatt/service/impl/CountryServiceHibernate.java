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
package it.av.eatt.service.impl;

import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CountryService;

import java.util.List;

import org.hibernate.criterion.Order;

/**
 * Implements operations on {@link Country}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class CountryServiceHibernate extends ApplicationServiceHibernate<Country> implements CountryService {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Country> getAll(){
        Order orderBYName = Order.asc(Country.NAME);
        return super.findByCriteria(orderBYName);
    }
}