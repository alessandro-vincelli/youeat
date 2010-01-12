package it.av.eatt.web.components;

import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CityService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Useful on adding new restaurant.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class CityAutocompleteBox extends AutoCompleteTextField<String> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private CityService cityService;

    /**
     * @param id
     * @param autoCompleteSettings
     * @param cityIAutoCompleteRenderer
     * @param ristoModel
     */
    public CityAutocompleteBox(String id, AutoCompleteSettings autoCompleteSettings, Model<String> cityModel) {
        super(id, cityModel, String.class, autoCompleteSettings);
        InjectorHolder.getInjector().inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> getChoices(String input) {
        List<String> choises = new ArrayList<String>();
        if (input.length() > 2) {
            Country country = ((DropDownChoice<Country>) getForm().get(Ristorante.COUNTRY)).getModelObject();
            if (country != null) {
                choises.addAll(cityService.findName(input, (Country) country, 25));
            } else {
                choises.addAll(cityService.findName(input, 25));
            }
        }
        return choises.iterator();
    }

}
