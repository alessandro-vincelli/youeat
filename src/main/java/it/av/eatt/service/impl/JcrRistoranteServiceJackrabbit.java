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

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.service.JcrRistoranteService;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional
public class JcrRistoranteServiceJackrabbit extends JcrApplicationServiceJackrabbit<Ristorante> implements JcrRistoranteService {

    /*
     * (non-Javadoc)
     * 
     * @see it.av.eatt.service.JcrRistoranteService#commit(it.av.eatt.ocm.model.Ristorante)
     */
    @Override
    public void commit(Ristorante risto) throws JackWicketException {
        if (StringUtils.isBlank(risto.getPath())) {
            risto.setPath(getBasePath() + "/" + risto.getId());
            super.insert(risto);
        } else {
            super.update(risto);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.av.eatt.service.JcrRistoranteService#getAllRevisionsById(long)
     */
    @Override
    public List<Ristorante> getAllRevisionsById(long id) throws JackWicketException {
        return super.getAllRevisions(getBasePath() + "/" + id);
    }

}
