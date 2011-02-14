package it.av.youeat.batch.system;

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.Ristorante;

/**
 * Utility bean to encapsulate DataRistorante and relative Ristorante to import 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class RistoranteBatchModel {

    /**
     * 
     * @param dataRistorante
     * @param ristorante
     */
    public RistoranteBatchModel(DataRistorante dataRistorante, Ristorante ristorante) {
        super();
        this.dataRistorante = dataRistorante;
        this.ristorante = ristorante;
    }

    private DataRistorante dataRistorante;
    private Ristorante ristorante;

    public DataRistorante getDataRistorante() {
        return dataRistorante;
    }

    public void setDataRistorante(DataRistorante dataRistorante) {
        this.dataRistorante = dataRistorante;
    }

    public Ristorante getRistorante() {
        return ristorante;
    }

    public void setRistorante(Ristorante ristorante) {
        this.ristorante = ristorante;
    }

}
