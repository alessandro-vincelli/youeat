package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.RistorantePicture;
import it.av.youeat.service.RistorantePictureService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
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
