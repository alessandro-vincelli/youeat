package it.av.youeat.web.components;

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.DataRistoranteService;
import it.av.youeat.web.page.RistoranteAddNewPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Useful on adding new restaurant.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class RistoranteAutocompleteBox extends AutoCompleteTextField<DataRistorante> {
    
    @SpringBean
    private DataRistoranteService dataRistoranteService;

    /**
     * @param id
     * @param autoCompleteSettings
     * @param dataRistoranteService
     */
    public RistoranteAutocompleteBox(String id, AutoCompleteSettings autoCompleteSettings) {
        super(id, autoCompleteSettings);
        InjectorHolder.getInjector().inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<DataRistorante> getChoices(String input) {
        Collection<DataRistorante> choises = new ArrayList<DataRistorante>();
        if (input.length() > 2) {
            String city = ((RistoranteAddNewPage) getPage()).getCityName();
            Country country = ((DropDownChoice<Country>) getForm().get(Ristorante.COUNTRY)).getModelObject();
            List<DataRistorante> lists = new ArrayList<DataRistorante>();
            if (city != null && country != null) {
                //lists.addAll(dataRistoranteService.find(input + "~ " + city + " " + ((Country) country).getName(), 25));
                lists.addAll(dataRistoranteService.freeTextSearch(input, city, ((Country) country).getIso2(), 25));
            } else {
                lists.addAll(dataRistoranteService.freeTextSearch(input, 25));
            }
            choises.addAll(lists);

        }
        return choises.iterator();
    }
}
