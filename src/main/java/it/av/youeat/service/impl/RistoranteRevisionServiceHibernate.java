package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.RistoranteRevision;
import it.av.youeat.service.RistoranteRevisionService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RistoranteRevisionServiceHibernate extends ApplicationServiceHibernate<RistoranteRevision> implements
        RistoranteRevisionService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RistoranteRevision insert(RistoranteRevision revision) {
        return save(revision);
    }
}
