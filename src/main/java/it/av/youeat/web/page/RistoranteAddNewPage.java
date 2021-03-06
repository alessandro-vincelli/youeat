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
package it.av.youeat.web.page;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CityService;
import it.av.youeat.service.CountryService;
import it.av.youeat.service.DataRistoranteService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.commons.AutocompleteUtils;
import it.av.youeat.web.components.ButtonOpenRisto;
import it.av.youeat.web.components.CityAutocompleteBox;
import it.av.youeat.web.components.RistoranteAutocompleteBox;
import it.av.youeat.web.modal.WarningOnAlreadyPresentRistoModalWindow;
import it.av.youeat.web.panel.ShowAlreadyPresentRistoPanel;
import it.av.youeat.web.security.SecuritySession;
import it.av.youeat.web.util.RistoranteUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * To add a new {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class RistoranteAddNewPage extends BasePage {

    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean(name = "dataRistoranteService")
    private DataRistoranteService dataRistoranteService;
    @SpringBean(name = "countryService")
    private CountryService countryService;
    @SpringBean
    private CityService cityService;

    private final AjaxFallbackLink<Ristorante> buttonClearForm;

    private Ristorante ristorante;
    private Form<Ristorante> form;
    private SubmitButton submitRestaurantRight;
    private SubmitButton submitRestaurantBottom;
    private String cityName = "";
    private BookmarkablePageLink<Ristorante> buttonOpenAddedRisto;
    private BookmarkablePageLink<Ristorante> buttonOpenAddedRistoRight;
    

    private ModalWindow modalWindow;
    /**
     * used by the ShowAlreadyPresentRistoPanel
     */
    private boolean persistTheRisto = false;
    /**
     * used by the ShowAlreadyPresentRistoPanel, if the user click on already present risto
     */
    private Ristorante ristoranteToRedirect;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public RistoranteAddNewPage() {
        ristorante = new Ristorante();
        ristorante.setCountry(getLoggedInUser().getCountry());
        form = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        add(getFeedbackPanel());
        AjaxFormComponentUpdatingBehavior updatingBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                try {
                    String ristoName = form.get(Ristorante.NAME).getDefaultModelObjectAsString();
                    ristoName = StringEscapeUtils.unescapeHtml(ristoName);
                    Country ristoCountry = ((DropDownChoice<Country>) form.get(Ristorante.COUNTRY)).getModelObject();
                    List<DataRistorante> ristosFound = new ArrayList<DataRistorante>(dataRistoranteService.getBy(ristoName,
                            cityName, ristoCountry));
                    if (!(ristosFound.isEmpty())) {
                        DataRistorante dataRistorante = ristosFound.get(0);
                        form.get(Ristorante.ADDRESS).setDefaultModelObject(dataRistorante.getAddress());
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
        CityAutocompleteBox city = new CityAutocompleteBox("city-autocomplete", AutocompleteUtils.getAutoCompleteSettings(),
                new Model<String>(cityName) {
                    @Override
                    public String getObject() {
                        return cityName;
                    }

                    @Override
                    public void setObject(String object) {
                        cityName = (String) object;
                    }
                });
        // With this component the city model is updated correctly after every change, fixing also the case of the city
        city.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String ristoName = getComponent().getDefaultModelObjectAsString();
                Country ristoCountry = ((DropDownChoice<Country>) form.get(Ristorante.COUNTRY)).getModelObject();
                ristoName = StringEscapeUtils.unescapeHtml(ristoName);
                City city = cityService.getByNameAndCountry(ristoName, ristoCountry);
                if (city != null) {
                    getComponent().setDefaultModelObject(city.getName());
                }
                if (target != null) {
                    target.addComponent(getComponent());
                }
            }
        });
        // city.add(new CityValidator());
        form.add(city);
        form.add(new TextField<String>(Ristorante.PROVINCE));
        form.add(new TextField<String>(Ristorante.POSTALCODE));
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
        form.add(new TextField<String>(Ristorante.MOBILE_PHONE_NUMBER));
        form.add(new TextField<String>(Ristorante.WWW));

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

        buttonOpenAddedRistoRight = new ButtonOpenRisto("buttonOpenAddedRistoRight", new Model<Ristorante>(ristorante), false);
        add(buttonOpenAddedRistoRight);

        submitRestaurantBottom = new SubmitButton("submitRestaurant", form);
        form.add(submitRestaurantBottom);
        submitRestaurantRight = new SubmitButton("submitRestaurantRight", form);
        add(submitRestaurantRight);

        add(form);

        modalWindow = new WarningOnAlreadyPresentRistoModalWindow("warningOnAlreadyPresentRistoModalWindow", this);
        add(modalWindow);
    }

    private class SubmitButton extends AjaxFallbackButton {

        public SubmitButton(String id, Form<Ristorante> form) {
            super(id, form);
            setOutputMarkupId(true);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, final Form form) {
            Ristorante ristorante = (Ristorante) form.getModelObject();
            City city = cityService.getByNameAndCountry(cityName, ristorante.getCountry());
            if (city == null) {
                error(getString("validatioError.city"));
                invalid();
            }
            ristorante.setCity(city);
            List<Ristorante> possiblExistingRisto = ristoranteService.freeTextSearchOnName(ristorante.getName(), ristorante.getCity());
            final Ristorante ristoTopersist = ristorante;
            if (possiblExistingRisto.size() > 0) {
                modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
                    public void onClose(AjaxRequestTarget target) {
                        if (persistTheRisto) {
                            persistRistorante(target, form, ristoTopersist);
                        } else {
                            getRequestCycle().setResponsePage(RistoranteViewPage.class,
                                    RistoranteUtil.createParamsForRisto(ristoranteToRedirect));
                        }
                    }
                });
                modalWindow.setContent(new ShowAlreadyPresentRistoPanel(modalWindow.getContentId(), (RistoranteAddNewPage) this
                        .getPage(), modalWindow, ristoTopersist, possiblExistingRisto));
                modalWindow.show(target);
            } else {
                persistRistorante(target, form, ristoTopersist);
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

    /**
     * @param target
     * @param form
     * @param ristorante
     */
    private void persistRistorante(AjaxRequestTarget target, Form form, Ristorante ristorante) {
        try {
            ristorante = ristoranteService.insert(ristorante, ((SecuritySession) getSession()).getLoggedInUser());
            getFeedbackPanel().info(getString("info.ristoranteadded", new Model<Ristorante>(ristorante)));
            form.setModelObject(ristorante);
        } catch (YoueatException e) {
            getFeedbackPanel().error(getString("genericErrorMessage"));
        }
        getForm().setEnabled(false);
        submitRestaurantBottom.setVisible(false);
        submitRestaurantRight.setVisible(false);
        buttonClearForm.setVisible(false);
        buttonOpenAddedRisto.setVisible(true);
        buttonOpenAddedRistoRight.setVisible(true);
        if (target != null) {
            target.addComponent(submitRestaurantRight);
            target.addComponent(getForm());
            target.addComponent(getFeedbackPanel());
            buttonOpenAddedRistoRight.remove();
            buttonOpenAddedRistoRight = new ButtonOpenRisto("buttonOpenAddedRistoRight", new Model<Ristorante>(ristorante), false);
            target.addComponent(buttonOpenAddedRistoRight);
            target.addComponent(buttonOpenAddedRisto);
        }
    }

    public boolean isPersistTheRisto() {
        return persistTheRisto;
    }

    public void setPersistTheRisto(boolean persistTheRisto) {
        this.persistTheRisto = persistTheRisto;
    }

    public void setRistoranteToRedirect(Ristorante ristoranteToRedirect) {
        this.ristoranteToRedirect = ristoranteToRedirect;
    }

    public String getCityName() {
        return cityName;
    }
}