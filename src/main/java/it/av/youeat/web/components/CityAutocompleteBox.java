package it.av.youeat.web.components;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CityService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.Injector;
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
        setRequired(true);
        setLabel(new Model<String>(getString("city")));
        Injector.get().inject(this);
        setOutputMarkupId(true);
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
                choises.addAll(cityService.findByName(input, (Country) country, 25));
            } else {
                choises.addAll(cityService.findName(input, 25));
            }
        }
        return choises.iterator();
    }

    @Override
    public void validate() {
        super.validate();
        Country country = ((Ristorante) (getForm().getModel().getObject())).getCountry();
        if (country != null && StringUtils.isNotBlank(getConvertedInput())) {
            try {
                City cityValue = cityService.getByNameAndCountry(getConvertedInput(), country);
                if (cityValue == null) {
                    error(getString("validatioError.city"));
                }
            } catch (YoueatException e) {
                error(getString("validatioError.city.error"));
            }
        }
    }

}
