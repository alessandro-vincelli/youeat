package it.av.youeat.batch.system;

import java.util.List;

import it.av.youeat.ocm.model.DataRistorante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;

public class ItemFailureLoggerListener extends ItemListenerSupport<DataRistorante, RistoranteBatchModel> {

    private static Logger log = LoggerFactory.getLogger(ItemFailureLoggerListener.class);

    @Override
    public void onReadError(Exception ex) {
        super.onReadError(ex);
        log.error("error reading an item", ex);
    }

    @Override
    public void onProcessError(DataRistorante item, Exception e) {
        super.onProcessError(item, e);
        log.error("error processing an item" + item, e);
    }

    @Override
    public void onWriteError(Exception ex, List<? extends RistoranteBatchModel> item) {
        super.onWriteError(ex, item);
        log.error("error writing an item", ex);
        log.error("error writing DataRistorante" + item.get(0).getDataRistorante());
        log.error("error writing Ristorante" + item.get(0).getRistorante());
    }

}
