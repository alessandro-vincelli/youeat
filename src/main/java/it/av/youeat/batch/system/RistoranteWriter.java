package it.av.youeat.batch.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.SocialType;
import it.av.youeat.service.DataRistoranteService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.RistoranteService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class RistoranteWriter implements ItemWriter<RistoranteBatchModel> {

    @Autowired
    private EaterService eaterService;
    @Autowired
    private DataRistoranteService dataRistoranteService;
    @Autowired
    private RistoranteService ristoranteService;

    private static Logger log = LoggerFactory.getLogger(RistoranteWriter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends RistoranteBatchModel> items) throws Exception {
        Eater user = eaterService.getBySocialUID("1303070414", SocialType.FACEBOOK);

        for (RistoranteBatchModel item : items) {
            ristoranteService.insert(item.getRistorante(), user);
            item.getDataRistorante().setImported(true);
            dataRistoranteService.update(item.getDataRistorante());
            log.info("Imported Risto:" + item.getRistorante());
        }
    }
}
