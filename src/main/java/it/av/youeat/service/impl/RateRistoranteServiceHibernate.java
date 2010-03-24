package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.RateOnRistorante;
import it.av.youeat.service.RateRistoranteService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class RateRistoranteServiceHibernate extends ApplicationServiceHibernate<RateOnRistorante> implements
        RateRistoranteService {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RateOnRistorante insert(RateOnRistorante rate) {
        return save(rate);
    }
}
