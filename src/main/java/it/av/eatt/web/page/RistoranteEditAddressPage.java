/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.eatt.web.page;

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.data.City;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CityService;
import it.av.eatt.service.CountryService;
import it.av.eatt.service.RistoranteService;
import it.av.eatt.web.commons.AutocompleteUtils;
import it.av.eatt.web.components.CityAutocompleteBox;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Edit address and contacts of {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN", "EDITOR" })
public class RistoranteEditAddressPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean(name = "countryService")
    private CountryService countryService;
    @SpringBean
    private CityService cityService;

    private Ristorante ristorante;
    private Form<Ristorante> form;
    private String cityName = "";

    /**
     * 
     * @param ristorante
     * @throws JackWicketException
     */
    public RistoranteEditAddressPage(Ristorante ristorante) throws JackWicketException {
        this(new PageParameters("ristoranteId=" + ristorante.getId()));
    }

    /**
     * 
     * @param parameters
     * @throws JackWicketException
     */
    public RistoranteEditAddressPage(PageParameters parameters) throws JackWicketException {

        String ristoranteId = parameters.getString("ristoranteId", "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {
            setRedirect(true);
            setResponsePage(getApplication().getHomePage());
        }

        form = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        form.setOutputMarkupId(true);
        form.add(new RequiredTextField<String>(Ristorante.NAME));
        form.add(new RequiredTextField<String>(Ristorante.ADDRESS));
        cityName = ristorante.getCity().getName();
        CityAutocompleteBox city = new CityAutocompleteBox("city-autocomplete", AutocompleteUtils
                .getAutoCompleteSettings(), new Model<String>(cityName) {
            @Override
            public String getObject() {
                return cityName;
            }

            @Override
            public void setObject(String object) {
                cityName = (String) object;
            }
        });
        // With this component the city model is updated correctly after every change
        city.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // TODO Auto-generated method stub
            }
        });
        city.add(new CityValidator());
        form.add(city);
        form.add(new RequiredTextField<String>(Ristorante.PROVINCE));
        form.add(new RequiredTextField<String>(Ristorante.POSTALCODE));
        DropDownChoice<Country> country = new DropDownChoice<Country>(Ristorante.COUNTRY, countryService.getAll(),
                new CountryChoiceRenderer());
        country.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        country.setRequired(true);
        form.add(country);
        form.add(new RequiredTextField<String>(Ristorante.PHONE_NUMBER));
        form.add(new TextField<String>(Ristorante.MOBILE_PHONE_NUMBER));
        form.add(new TextField<String>(Ristorante.FAX_NUMBER));

        form.add(new AjaxFallbackLink<Ristorante>("buttonClearForm", new Model<Ristorante>(ristorante)) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                form.setModelObject(new Ristorante());
                if (target != null) {
                    target.addComponent(form);
                }
            }
        });
        form.add(new SubmitButton("submitRestaurant", form));
        add(form);
        add(new SubmitButton("submitRestaurantRight", form));
    }

    private class SubmitButton extends AjaxFallbackButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.getAttributes().put("value", getString("button.update"));
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                if (StringUtils.isNotBlank(ristorante.getId())) {
                    City city = cityService.getByNameAndCountry(cityName, ristorante.getCountry());
                    if (city == null) {
                        error("The city doesn't exist");
                        invalid();
                    }
                    ristorante.setCity(city);
                    ristorante = ristoranteService.update(ristorante, getLoggedInUser());
                    getFeedbackPanel().info(getString("info.ristoranteupdated"));
                } else {
                    getFeedbackPanel().error(getString("error.onUpdate"));
                }
                form.setModelObject(ristorante);
            } catch (JackWicketException e) {
                getFeedbackPanel().error("ERROR" + e.getMessage());
            }
            if (target != null) {
                target.addComponent(form);
                target.addComponent(getFeedbackPanel());
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form form) {
            getFeedbackPanel().anyErrorMessage();
            target.addComponent(getFeedbackPanel());
        }
    }

    public final Form<Ristorante> getForm() {
        return form;
    }

    private class CountryChoiceRenderer implements IChoiceRenderer<Country> {
        @Override
        public Object getDisplayValue(Country object) {
            return object.getName();
        }

        @Override
        public String getIdValue(Country object, int index) {
            return object.getId();
        }
    }

    private class CityValidator implements IValidator<String> {
        @Override
        public void validate(IValidatable<String> validatable) {
            Country country = form.getModel().getObject().getCountry();
            if (country != null) {
                try {
                    City cityValue = cityService.getByNameAndCountry(validatable.getValue(), country);
                    if (cityValue == null) {
                        validatable.error(new ValidationError().addMessageKey("validatioError.city"));
                    }
                } catch (JackWicketException e) {
                    validatable.error(new ValidationError().addMessageKey("validatioError.city.error"));
                }
            }
        }
    }

}
