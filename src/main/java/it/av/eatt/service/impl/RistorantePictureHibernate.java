package it.av.eatt.service.impl;

import it.av.eatt.ocm.model.RistorantePicture;
import it.av.eatt.service.RistorantePictureService;

public class RistorantePictureHibernate extends ApplicationServiceHibernate<RistorantePicture> implements
        RistorantePictureService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(RistorantePicture object) {
        super.remove(getByID(object.getId()));
    }

}
