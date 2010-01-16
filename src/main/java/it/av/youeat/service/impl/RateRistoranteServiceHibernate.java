package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.RateOnRistorante;
import it.av.youeat.service.RateRistoranteService;

public class RateRistoranteServiceHibernate extends ApplicationServiceHibernate<RateOnRistorante> implements
        RateRistoranteService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RateOnRistorante insert(RateOnRistorante rate){
        return save(rate);
    }
}
