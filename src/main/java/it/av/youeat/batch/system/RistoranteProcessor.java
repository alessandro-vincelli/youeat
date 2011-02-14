package it.av.youeat.batch.system;

import static it.av.youeat.batch.system.RistoranteBatchUtil.createAddress;
import static it.av.youeat.batch.system.RistoranteBatchUtil.createName;
import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CityService;
import it.av.youeat.service.CountryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class RistoranteProcessor implements ItemProcessor<DataRistorante, RistoranteBatchModel> {

    private static Logger log = LoggerFactory.getLogger(RistoranteProcessor.class);
    @Autowired
    private CityService cityService;
    @Autowired
    private CountryService countryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public RistoranteBatchModel process(DataRistorante item) throws Exception {
        Country country = countryService.getByIso2("IT");
        log.info("processing: " + item);
        Ristorante ristorante = new Ristorante();
        ristorante.setName(createName(item.getName()));
        ristorante.setCountry(country);
        ristorante.setCity(cityService.find(item.getCity(), 1).get(0));
        ristorante.setAddress(createAddress(item.getAddress()));
        ristorante.setPostalCode(item.getPostalCode());
        ristorante.setProvince(item.getProvince());
        ristorante.setWww(item.getWww());
        ristorante.setMobilePhoneNumber(item.getMobilePhoneNumber());
        ristorante.setPhoneNumber(item.getPhoneNumber());
        log.info("processed: " + ristorante);
        return new RistoranteBatchModel(item, ristorante);
    }

}
