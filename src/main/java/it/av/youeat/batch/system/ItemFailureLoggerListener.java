package it.av.youeat.batch.system;

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.service.DataRistoranteService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class ItemFailureLoggerListener extends ItemListenerSupport<DataRistorante, RistoranteBatchModel> {

    @Autowired
    private DataRistoranteService dataRistoranteService;
    private static Logger log = LoggerFactory.getLogger(ItemFailureLoggerListener.class);

    @Override
    public void onReadError(Exception ex) {
        super.onReadError(ex);
        log.error("error reading an item", ex);
    }

    @Override
    public void onProcessError(DataRistorante item, Exception e) {
        super.onProcessError(item, e);
        item.setImportfails(true);
        dataRistoranteService.update(item);
        log.error("error processing the Risto" + item, e);
    }

    @Override
    public void onWriteError(Exception ex, List<? extends RistoranteBatchModel> item) {
        super.onWriteError(ex, item);
        log.error("error writing an item", ex);
        log.error("error writing DataRistorante" + item.get(0).getDataRistorante());
        log.error("error writing Ristorante" + item.get(0).getRistorante());
    }

}
