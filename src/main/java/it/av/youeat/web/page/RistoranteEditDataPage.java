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

import it.av.youeat.YoueatConcurrentModificationException;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistoranteDescriptionI18n;
import it.av.youeat.ocm.model.Tag;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.LanguageService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.service.TagService;
import it.av.youeat.web.Locales;
import it.av.youeat.web.components.ButtonOpenRisto;
import it.av.youeat.web.components.TagBox;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.springframework.util.Assert;

/**
 * Edit a {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class RistoranteEditDataPage extends BasePage {

    @SpringBean
    private RistoranteService ristoranteService;
    @SpringBean
    private TagService tagService;
    @SpringBean
    private LanguageService languageService;

    private Ristorante ristorante;
    private Form<Ristorante> form;
    private final ListView<RistoranteDescriptionI18n> descriptions;
    private Language actualDescriptionLanguage;
    private WebMarkupContainer descriptionsContainer;
    private WebMarkupContainer descriptionLinksContainer;

    /**
     * 
     * @param parameters
     * @throws YoueatException
     */
    public RistoranteEditDataPage(PageParameters parameters) throws YoueatException {

        String ristoranteId = parameters.getString(YoueatHttpParams.RISTORANTE_ID, "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {
            throw new RestartResponseAtInterceptPageException(getApplication().getHomePage());
        }
        actualDescriptionLanguage = getInitialLanguage();
        ristorante = ristorante.addDescLangIfNotPresent(actualDescriptionLanguage);
        form = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        form.setOutputMarkupId(true);
        form.add(new RequiredTextField<String>(Ristorante.NAME));

        form.add(new TextField<String>(Ristorante.WWW));
        form.add(new TextField<String>(Ristorante.EMAIL).add(EmailAddressValidator.getInstance()));
        form.add(new TagBox(new Model<String>(""), "tagBox", ristorante));

        // form.add(new CheckBox("types.ristorante"));
        // form.add(new CheckBox("types.pizzeria"));
        // form.add(new CheckBox("types.bar"));

        form.add(new ListView<Tag>(Ristorante.TAGS) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Tag> item) {
                item.add(new Label("tagItem", item.getModelObject().getTag()));
                item.add(new AjaxFallbackLink<String>("buttonTagItemRemove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getList().remove(getParent().getDefaultModelObject());
                        if (target != null) {
                            target.addComponent(form);
                        }
                    }
                });
            }
        });
        descriptionLinksContainer = new WebMarkupContainer("descriptionLinksContainer");
        descriptionLinksContainer.setOutputMarkupId(true);
        form.add(descriptionLinksContainer);
        ListView<Language> descriptionsLinks = new ListView<Language>("descriptionLinks", languageService.getAll()) {
            @Override
            protected void populateItem(final ListItem<Language> item) {
                
                if (actualDescriptionLanguage.getCountry().equals(item.getModelObject().getCountry())) {
                    item.add(new AttributeAppender("class", new Model<String>("ui-tabs-selected ui-state-active"), " "));
                }
                
                item.add(new AjaxFallbackButton("descriptionLink", form) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        actualDescriptionLanguage = item.getModelObject();
                        ristorante = ristorante.addDescLangIfNotPresent(actualDescriptionLanguage);
                        descriptions.removeAll();
                        if (target != null) {
                            target.addComponent(descriptionsContainer);
                            target.addComponent(descriptionLinksContainer);
                        }
                    }
                }.add(new Label("linkName", getString(item.getModelObject().getCountry()))));
            }
        };
        descriptionLinksContainer.add(descriptionsLinks);
        descriptionsContainer = new WebMarkupContainer("descriptionsContainer");
        descriptionsContainer.setOutputMarkupId(true);
        form.add(descriptionsContainer);
        descriptions = new ListView<RistoranteDescriptionI18n>("descriptions") {
            @Override
            protected void populateItem(ListItem<RistoranteDescriptionI18n> item) {
                boolean visible = actualDescriptionLanguage.equals(item.getModelObject().getLanguage());
                item.add(new TextArea<String>(RistoranteDescriptionI18n.DESCRIPTION, new PropertyModel<String>(item
                        .getModelObject(), RistoranteDescriptionI18n.DESCRIPTION)).setVisible(visible));
            }
        };
        descriptions.setReuseItems(false);
        descriptions.setOutputMarkupId(true);
        descriptionsContainer.add(descriptions);
        // form.add(new DropDownChoice<EaterProfile>("userProfile", new
        // ArrayList<EaterProfile>(userProfileService.getAll()), new UserProfilesList()).setOutputMarkupId(true));

        form.add(new AjaxFallbackButton("addTag", form) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                String tagValue = ((TagBox) form.get("tagBox")).getModelObject();
                if (StringUtils.isNotBlank(tagValue)) {
                    Ristorante risto = ((Ristorante) form.getModelObject());
                    try {
                        risto.getTags().add(tagService.insert(tagValue));
                        form.setModelObject(risto);
                        if (target != null) {
                            target.addComponent(form);
                        }
                    } catch (YoueatException e) {
                        error("genericErrorMessage");
                        if (target != null) {
                            target.addComponent(getFeedbackPanel());
                        }
                    }
                }
                // after clean up the tagBox
                ((TagBox) form.get("tagBox")).setModelObject(null);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if (target != null) {
                    target.addComponent(getFeedbackPanel());
                }
            }
        });

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
        ButtonOpenRisto buttonOpenAddedRisto = new ButtonOpenRisto("buttonOpenAddedRisto", new Model<Ristorante>(
                ristorante), true);
        add(buttonOpenAddedRisto);

        ButtonOpenRisto buttonOpenAddedRistoRight = new ButtonOpenRisto("buttonOpenAddedRistoRight",
                new Model<Ristorante>(ristorante), true);
        add(buttonOpenAddedRistoRight);
    }

    private class SubmitButton extends AjaxFallbackButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                if (StringUtils.isNotBlank(ristorante.getId())) {
                    ristoranteService.update((Ristorante)form.getModelObject(), getLoggedInUser());
                    ristorante = ristoranteService.getByID(ristorante.getId());
                    form.getModel().setObject(ristorante);
                    getFeedbackPanel().info(getString("info.ristoranteupdated"));
                } else {
                    getFeedbackPanel().error(getString("error.onUpdate"));
                }
                form.setModelObject(ristorante);
            } catch (YoueatConcurrentModificationException e) {
                getFeedbackPanel().error(getString("error.concurrentModification"));
            } catch (YoueatException e) {
                getFeedbackPanel().error(getString("genericErrorMessage"));
            }
            if (target != null) {
                target.addComponent(form);
                target.addComponent(getFeedbackPanel());
                //target.appendJavascript(new Effect.Shake( this  ).toJavascript()); 
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

    private Language getInitialLanguage() throws YoueatException {
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
        return lang;
    }
}
