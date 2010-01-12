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

import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.DataRistorante;
import it.av.eatt.ocm.model.Language;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.RistoranteDescriptionI18n;
import it.av.eatt.ocm.model.data.City;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CityService;
import it.av.eatt.service.CountryService;
import it.av.eatt.service.DataRistoranteService;
import it.av.eatt.service.LanguageService;
import it.av.eatt.service.RistoranteService;
import it.av.eatt.web.Locales;
import it.av.eatt.web.commons.AutocompleteUtils;
import it.av.eatt.web.components.ButtonOpenRisto;
import it.av.eatt.web.components.CityAutocompleteBox;
import it.av.eatt.web.components.RistoranteAutocompleteBox;
import it.av.eatt.web.security.SecuritySession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.util.Assert;

/**
 * To add a new {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN", "EDITOR" })
public class RistoranteAddNewPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean(name = "dataRistoranteService")
    private DataRistoranteService dataRistoranteService;
    @SpringBean(name = "countryService")
    private CountryService countryService;
    @SpringBean
    private CityService cityService;
    @SpringBean
    private LanguageService languageService;

    private final AjaxFallbackLink<Ristorante> buttonClearForm;

    private Ristorante ristorante;
    private Form<Ristorante> form;
    private SubmitButton submitRestaurantRight;
    private String cityName = "";
    private AjaxFallbackLink<Ristorante> buttonOpenAddedRisto;
    private AjaxFallbackLink<Ristorante> buttonOpenAddedRistoRight;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public RistoranteAddNewPage() throws YoueatException {
        ristorante = new Ristorante();
        ristorante.addDescriptions(getDescriptionI18n());
        form = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        add(getFeedbackPanel());
        AjaxFormComponentUpdatingBehavior updatingBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                try {
                    String ristoName = form.get(Ristorante.NAME).getDefaultModelObjectAsString();
                    ristoName = StringEscapeUtils.unescapeHtml(ristoName);
                    Country ristoCountry = ((DropDownChoice<Country>) form.get(Ristorante.COUNTRY)).getModelObject();
                    List<DataRistorante> ristosFound = new ArrayList<DataRistorante>(dataRistoranteService.getBy(
                            ristoName, cityName, ristoCountry));
                    if (!(ristosFound.isEmpty())) {
                        DataRistorante dataRistorante = ristosFound.get(0);
                        form.get(Ristorante.ADDRESS).setDefaultModelObject(dataRistorante.getAddress());
                        form.get(Ristorante.FAX_NUMBER).setDefaultModelObject(dataRistorante.getFaxNumber());
                        form.get(Ristorante.PHONE_NUMBER).setDefaultModelObject(dataRistorante.getPhoneNumber());
                        form.get(Ristorante.POSTALCODE).setDefaultModelObject(dataRistorante.getPostalCode());
                        form.get(Ristorante.PROVINCE).setDefaultModelObject(dataRistorante.getProvince());
                        form.get(Ristorante.WWW).setDefaultModelObject(dataRistorante.getWww());
                        info(new StringResourceModel("info.autocompletedRistoSucces", form, null).getString());
                        target.addComponent(getFeedbackPanel());
                        target.addComponent(form);
                    }
                } catch (Exception e) {
                    error(new StringResourceModel("genericErrorMessage", form, null).getString());
                    target.addComponent(getFeedbackPanel());
                }
            }
        };
        form.setOutputMarkupId(true);
        final RistoranteAutocompleteBox ristoName = new RistoranteAutocompleteBox(Ristorante.NAME, AutocompleteUtils
                .getAutoCompleteSettings());
        ristoName.setRequired(true);
        ristoName.add(updatingBehavior);
        form.add(ristoName);
        form.add(new RequiredTextField<String>(Ristorante.ADDRESS));
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
        form.add(new TextField<String>(Ristorante.PHONE_NUMBER));
        form.add(new TextField<String>(Ristorante.FAX_NUMBER));
        form.add(new TextField<String>(Ristorante.MOBILE_PHONE_NUMBER));
        form.add(new TextField<String>(Ristorante.WWW));
        // form.add(new CheckBox("types.ristorante"));
        // form.add(new CheckBox("types.pizzeria"));
        // form.add(new CheckBox("types.bar"));

        ListView<RistoranteDescriptionI18n> descriptions = new ListView<RistoranteDescriptionI18n>("descriptions") {
            @Override
            protected void populateItem(ListItem<RistoranteDescriptionI18n> item) {
                item.add(new Label(RistoranteDescriptionI18n.LANGUAGE, getString(item.getModelObject().getLanguage()
                        .getCountry())));
                item.add(new TextArea<String>(RistoranteDescriptionI18n.DESCRIPTION, new PropertyModel<String>(item
                        .getModelObject(), RistoranteDescriptionI18n.DESCRIPTION)));
            }
        };
        descriptions.setReuseItems(true);
        form.add(descriptions);

        buttonClearForm = new AjaxFallbackLink<Ristorante>("buttonClearForm", new Model<Ristorante>(ristorante)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                form.setModelObject(new Ristorante());
                target.addComponent(form);
            }
        };

        form.add(buttonClearForm);

        buttonOpenAddedRisto = new ButtonOpenRisto("buttonOpenAddedRisto", new Model<Ristorante>(ristorante), false);
        add(buttonOpenAddedRisto);

        buttonOpenAddedRistoRight = new ButtonOpenRisto("buttonOpenAddedRistoRight", new Model<Ristorante>(ristorante),
                false);
        add(buttonOpenAddedRistoRight);

        form.add(new SubmitButton("submitRestaurant", form));
        submitRestaurantRight = new SubmitButton("submitRestaurantRight", form);
        submitRestaurantRight.setOutputMarkupId(true);
        add(submitRestaurantRight);

        // OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior() {
        // @Override
        // protected void onUpdate(AjaxRequestTarget target) {
        // // label.setModelObject(getValue(city.getModelObjectAsString()));
        // // target.addComponent(label);
        // }
        // };
        // city.add(onChangeAjaxBehavior);

        add(form);
    }

    private class SubmitButton extends AjaxFallbackButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                Ristorante ristorante = (Ristorante) form.getModelObject();
                City city = cityService.getByNameAndCountry(cityName, ristorante.getCountry());
                if (city == null) {
                    error(getString("validatioError.city"));
                    invalid();
                }
                ristorante.setCity(city);
                ristorante = ristoranteService.insert(ristorante, ((SecuritySession) getSession()).getLoggedInUser());
                getFeedbackPanel().info(getString("info.ristoranteadded", new Model<Ristorante>(ristorante)));
                form.setModelObject(ristorante);
            } catch (YoueatException e) {
                getFeedbackPanel().error("ERROR" + e.getMessage());
            }
            getForm().setEnabled(false);
            setVisible(false);
            submitRestaurantRight.setVisible(false);
            buttonClearForm.setVisible(false);
            buttonOpenAddedRisto.setVisible(true);
            buttonOpenAddedRistoRight.setVisible(true);
            if (target != null) {
                target.addComponent(submitRestaurantRight);
                target.addComponent(getForm());
                target.addComponent(getFeedbackPanel());
                target.addComponent(buttonOpenAddedRistoRight);
                target.addComponent(buttonOpenAddedRisto);
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
                } catch (YoueatException e) {
                    validatable.error(new ValidationError().addMessageKey("validatioError.city.error"));
                }
            }
        }
    }

    private RistoranteDescriptionI18n getDescriptionI18n() throws YoueatException {
        Locale locale = Locales.getSupportedLocale(getLocale());
        // TODO create a getByLanguage or Country
        List<Language> langs = languageService.getAll();
        Language lang = null;
        for (Language language : langs) {
            if (language.getCountry().equals(locale.getCountry())) {
                lang = language;
            }
        }
        Assert.notNull(lang);
        RistoranteDescriptionI18n descriptionI18n = new RistoranteDescriptionI18n(lang);
        return descriptionI18n;
    }

    public String getCityName() {
        return cityName;
    }
}