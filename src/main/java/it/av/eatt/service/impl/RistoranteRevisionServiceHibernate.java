package it.av.eatt.service.impl;

import it.av.eatt.ocm.model.RistoranteRevision;
import it.av.eatt.service.RistoranteRevisionService;

public class RistoranteRevisionServiceHibernate extends ApplicationServiceHibernate<RistoranteRevision> implements
        RistoranteRevisionService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RistoranteRevision insert(RistoranteRevision revision){
        return save(revision);
    }
}
