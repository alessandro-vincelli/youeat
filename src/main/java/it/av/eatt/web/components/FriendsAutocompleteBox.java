package it.av.eatt.web.components;

import it.av.eatt.ocm.model.DataRistorante;
import it.av.eatt.service.DataRistoranteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;

/**
 * Useful on adding new restaurant.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class FriendsAutocompleteBox extends AutoCompleteTextField<DataRistorante> {
    private static final long serialVersionUID = 1L;
    private DataRistoranteService dataRistoranteService;

    /**
     * @param id
     * @param dataRistoranteService
     */
    public FriendsAutocompleteBox(String id, DataRistoranteService dataRistoranteService) {
        super(id);
        this.dataRistoranteService = dataRistoranteService;
    }

    /**
     * TODO
     * {@inheritDoc}
     */
    @Override
    protected Iterator<DataRistorante> getChoices(String input) {
        Collection<DataRistorante> choises = new ArrayList<DataRistorante>();
//        try {
//            String city =  getForm().get(Ristorante.CITY).getDefaultModelObjectAsString();
//            String country = getForm().get(Ristorante.COUNTRY).getDefaultModelObjectAsString();
//            List<DataRistorante> lists = new ArrayList<DataRistorante>();
//            if(!city.isEmpty() &&  !country.isEmpty() ){
//                //
//                //lists.addAll(dataRistoranteService.find(input + "%", city, country));
//            }
//            else{
//                //lists.addAll(dataRistoranteService.find(input + "%"));
//            }
//            choises.addAll(lists);
//        } catch (JackWicketException e) {
//            throw new JackWicketRunTimeException(e);
//        }
//        return choises.iterator();
        throw new RuntimeException("not yet implemented");
    }

}
