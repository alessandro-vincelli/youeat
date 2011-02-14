package it.av.youeat.batch.system;

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.service.DataRistoranteService;

import java.util.Collection;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class DataRistoranteReader implements ItemReader<DataRistorante> {

    @Autowired
    private DataRistoranteService dataRistoranteService;

    @Override
    public DataRistorante read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Collection<DataRistorante> toBeImported = dataRistoranteService.getToBeImported(1);
        if (!toBeImported.isEmpty()) {
            return toBeImported.iterator().next();
        } else {
            return null;
        }
    }

}
