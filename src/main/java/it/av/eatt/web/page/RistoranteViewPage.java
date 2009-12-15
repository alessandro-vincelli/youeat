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
import it.av.eatt.ocm.model.ActivityRistorante;
import it.av.eatt.ocm.model.Language;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.RistoranteDescriptionI18n;
import it.av.eatt.ocm.model.RistorantePicture;
import it.av.eatt.ocm.model.Tag;
import it.av.eatt.service.ActivityRistoranteService;
import it.av.eatt.service.LanguageService;
import it.av.eatt.service.RistoranteService;
import it.av.eatt.web.Locales;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.extensions.rating.RatingPanel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.Assert;

/**
 * The page shows all the {@link Ristorante} informations.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteViewPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean
    private LanguageService languageService;
    @SpringBean
    private ActivityRistoranteService activityService;
    
    private Ristorante ristorante = new Ristorante();;

    private ModalWindow revisionsPanel;
    private boolean hasVoted = Boolean.FALSE;
    private Language actualDescriptionLanguage;
    private ListView<RistoranteDescriptionI18n> descriptions;
    private WebMarkupContainer descriptionsContainer;
    private Form<Ristorante> form;
    private WebMarkupContainer descriptionLinksContainer;
    private ListView<RistorantePicture> picturesList;
    private Label asfavouriteLabel;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws JackWicketException
     */
    public RistoranteViewPage(PageParameters parameters) throws JackWicketException {
        actualDescriptionLanguage = getInitialLanguage();
        String ristoranteId = parameters.getString("ristoranteId", "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {

            setRedirect(true);
            setResponsePage(getApplication().getHomePage());
        }

        form = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        add(form);
        form.setOutputMarkupId(true);
        form.add(new Label(Ristorante.NAME));

        Label typeRistoranteLabel = new Label("typeRistoranteLabel", getString("type.Ristorante"));
        typeRistoranteLabel.setVisible(ristorante.getTypes().isRistorante());
        form.add(typeRistoranteLabel);
        Label typePizzeriaLabel = new Label("typePizzeriaLabel", getString("type.Pizzeria"));
        typePizzeriaLabel.setVisible(ristorante.getTypes().isPizzeria());
        form.add(typePizzeriaLabel);
        Label typeBarLabel = new Label("typeBarLabel", getString("type.Bar"));
        typeBarLabel.setVisible(ristorante.getTypes().isBar());
        form.add(typeBarLabel);

        form.add(new SmartLinkLabel(Ristorante.WWW));
        form.add(new ListView<Tag>(Ristorante.TAGS) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Tag> item) {
                item.add(new Label("tagItem", item.getModelObject().getTag()));
            }
        });
        descriptionLinksContainer = new WebMarkupContainer("descriptionLinksContainer");
        descriptionLinksContainer.setOutputMarkupId(true);
        form.add(descriptionLinksContainer);
        ListView<Language> descriptionsLinks = new ListView<Language>("descriptionLinks", languageService.getAll()) {
            @Override
            protected void populateItem(final ListItem<Language> item) {
                item.add(new AjaxFallbackButton("descriptionLink", form) {

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        boolean langpresent = isDescriptionPresentOnTheGivenLanguage(ristorante, item.getModelObject());
                        HashMap<String, String> tagAttrs = new HashMap<String, String>();
                        if (!langpresent) {
                            tagAttrs.put("title", getString("descriptionNotAvailableLang"));
                            tagAttrs.put("class", "descriptionNotAvailableLang");
                        }
                        if (actualDescriptionLanguage.getCountry().equals(item.getModelObject().getCountry())) {
                            if (tagAttrs.containsKey("class")) {
                                tagAttrs.put("class", tagAttrs.get("class").concat(
                                        " descriptionLink descriptionLinkActive"));
                            } else {
                                tagAttrs.put("class", "descriptionLink descriptionLinkActive");
                            }
                        }
                        tag.getAttributes().putAll(tagAttrs);
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        List<RistoranteDescriptionI18n> descs = ristorante.getDescriptions();
                        boolean langpresent = isDescriptionPresentOnTheGivenLanguage(ristorante, item.getModelObject());
                        for (RistoranteDescriptionI18n ristoranteDescriptionI18n : descs) {
                            if (ristoranteDescriptionI18n.getLanguage().equals(item.getModelObject())) {
                                langpresent = true;
                            }
                        }
                        if (!(langpresent)) {
                            ristorante.addDescriptions(new RistoranteDescriptionI18n(item.getModelObject()));
                        }
                        actualDescriptionLanguage = item.getModelObject();
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
                if (item.getModelObject().getDescription() == null || item.getModelObject().getDescription().isEmpty()) {
                    item.add(new Label(RistoranteDescriptionI18n.DESCRIPTION, getString("descriptionNotAvailableLang"))
                            .setVisible(visible));
                } else {
                    item.add(new MultiLineLabel(RistoranteDescriptionI18n.DESCRIPTION, new PropertyModel<String>(item
                            .getModelObject(), RistoranteDescriptionI18n.DESCRIPTION)).setVisible(visible));
                }
            }
        };
        descriptionsContainer.add(descriptions);
        // form.add(new DropDownChoice<EaterProfile>("userProfile", new
        // ArrayList<EaterProfile>(userProfileService.getAll()), new UserProfilesList()).setOutputMarkupId(true));
        form.add(new Label("revisionNumber"));

        Form<Ristorante> formAddress = new Form<Ristorante>("ristoranteAddressForm",
                new CompoundPropertyModel<Ristorante>(ristorante));
        add(formAddress);
        formAddress.add(new Label(Ristorante.ADDRESS));
        formAddress.add(new Label(Ristorante.CITY));
        formAddress.add(new Label(Ristorante.PROVINCE));
        formAddress.add(new Label(Ristorante.POSTALCODE));
        formAddress.add(new Label(Ristorante.COUNTRY));
        formAddress.add(new Label(Ristorante.MOBILE_PHONE_NUMBER));
        formAddress.add(new Label(Ristorante.PHONE_NUMBER));
        formAddress.add(new Label(Ristorante.FAX_NUMBER));
        formAddress.add(new RatingPanel("rating1", new PropertyModel<Integer>(getRistorante(), "rating"),
                new Model<Integer>(5), new PropertyModel<Integer>(getRistorante(), "rates.size"),
                new PropertyModel<Boolean>(this, "hasVoted"), true) {
            @Override
            protected boolean onIsStarActive(int star) {
                return star < ((int) (getRistorante().getRating() + 0.5));
            }

            @Override
            protected void onRated(int rating, AjaxRequestTarget target) {
                try {
                    setHasVoted(Boolean.TRUE);
                    ristoranteService.addRate(getRistorante(), getLoggedInUser(), rating);
                } catch (JackWicketException e) {
                    error(e);
                }
            }
        });

        AjaxFallbackLink<String> editRistorante = new AjaxFallbackLink<String>("editRistorante") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    setResponsePage(new RistoranteEditAddressPage(getRistorante()));
                } catch (JackWicketException e) {
                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                }
            }
        };
        editRistorante.setOutputMarkupId(true);
        if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                RistoranteEditAddressPage.class)) {
            editRistorante.setVisible(true);
        } else {
            editRistorante.setVisible(false);
        }
        add(editRistorante);

        AjaxFallbackLink<String> editDataRistorante = new AjaxFallbackLink<String>("editDataRistorante") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    setResponsePage(new RistoranteEditDataPage(getRistorante()));
                } catch (JackWicketException e) {
                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                }
            }
        };
        editDataRistorante.setOutputMarkupId(true);
        add(editDataRistorante);

        AjaxFallbackLink<String> editPictures = new AjaxFallbackLink<String>("editPictures") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    setResponsePage(new RistoranteEditPicturePage(getRistorante()));
                } catch (JackWicketException e) {
                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                }
            }
        };
        editPictures.setOutputMarkupId(true);
        add(editPictures);

        picturesList = new ListView<RistorantePicture>("picturesList", ristorante.getActivePictures()) {

            @Override
            protected void populateItem(final ListItem<RistorantePicture> item) {
                item.add(new Image("picture", new DynamicImageResource() {
                    @Override
                    protected byte[] getImageData() {
                        return item.getModelObject().getPicture();
                    }
                }));
            }
        };
        form.add(picturesList);
        add(revisionsPanel = new ModalWindow("revisionsPanel"));
        revisionsPanel.setWidthUnit("%");
        revisionsPanel.setInitialHeight(450);
        revisionsPanel.setInitialWidth(100);
        revisionsPanel.setResizable(false);
        revisionsPanel.setContent(new RistoranteRevisionsPanel(revisionsPanel.getContentId(), getFeedbackPanel()));
        revisionsPanel.setTitle("Revisions list");
        revisionsPanel.setCookieName("SC-revisionLists");

        revisionsPanel.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                return true;
            }
        });

        revisionsPanel.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {

            }
        });

        add(new AjaxLink("showsAllRevisions") {
            public void onClick(AjaxRequestTarget target) {
                ((RistoranteRevisionsPanel) revisionsPanel.get(revisionsPanel.getContentId()))
                        .refreshRevisionsList(ristorante);
                revisionsPanel.show(target);
            }
        });
        
        add(new AjaxFallbackLink<String>("tried") {
            public void onClick(AjaxRequestTarget target) {
                if (getLoggedInUser() != null){
                    activityService.save(new ActivityRistorante(getLoggedInUser(), ristorante, ActivityRistorante.TYPE_TRIED));
                    info(getString("info.IateHere", new Model<Ristorante>(ristorante)));
                }
                else {
                    info(getString("action.notlogged"));
                }
                target.addComponent(getFeedbackPanel());
            }
        });
        
        AjaxLink<String> asfavourite =  new AjaxLink<String>("asfavourite") {
            public void onClick(AjaxRequestTarget target) {
                if (getLoggedInUser() != null){
                    if(activityService.isFavouriteRisto(getLoggedInUser(), ristorante)){
                        activityService.save(new ActivityRistorante(getLoggedInUser(), ristorante, ActivityRistorante.TYPE_REMOVED_AS_FAVOURITE));
                        info(getString("action.removedAsFavourite", new Model<Ristorante>(ristorante)));
                        asfavouriteLabel.setDefaultModelObject(getString("button.addAsFavourite"));
                    }
                    else{
                        activityService.save(new ActivityRistorante(getLoggedInUser(), ristorante, ActivityRistorante.TYPE_ADDED_AS_FAVOURITE));
                        info(getString("action.addedAsFavourite", new Model<Ristorante>(ristorante)));
                        asfavouriteLabel.setDefaultModelObject(getString("button.removeAsFavourite"));
                    }                    
                }
                else {
                    info(getString("action.notlogged"));
                }
                target.addComponent(asfavouriteLabel);
                target.addComponent(getFeedbackPanel());
            }
        };
        add(asfavourite);
        asfavouriteLabel = new Label("asfavouriteLabel", getString("button.addAsFavourite"));
        if(activityService.isFavouriteRisto(getLoggedInUser(), ristorante)){
            asfavouriteLabel.setDefaultModelObject(getString("button.removeAsFavourite"));
        }
        asfavouriteLabel.setOutputMarkupId(true);
        asfavourite.add(asfavouriteLabel);

        setHasVoted(ristoranteService.hasUsersAlreadyRated(getRistorante(), getLoggedInUser())
                || getLoggedInUser() == null);

    }

    public RistoranteViewPage(Ristorante ristorante) throws JackWicketException {
        this(new PageParameters("ristoranteId=" + ristorante.getId()));
    }

    public final Ristorante getRistorante() {
        return ristorante;
    }

    public final void setRistorante(Ristorante ristorante) {
        this.ristorante = ristorante;
    }

    /**
     * @return the hasVoted
     */
    private boolean isHasVoted() {
        return hasVoted;
    }

    /**
     * @param hasVoted the hasVoted to set
     */
    private void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    private Language getInitialLanguage() throws JackWicketException {
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

    /**
     * Chek if the given risto has a not empty description on the given language
     * 
     * @param ristorante the risto to verify
     * @param language the language to check
     * @return true if the risto has desc on the given lang
     */
    private boolean isDescriptionPresentOnTheGivenLanguage(Ristorante ristorante, Language language) {
        List<RistoranteDescriptionI18n> descs = ristorante.getDescriptions();
        boolean langpresent = false;
        for (RistoranteDescriptionI18n ristoranteDescriptionI18n : descs) {
            if (ristoranteDescriptionI18n.getLanguage().equals(language)
                    && ristoranteDescriptionI18n.getDescription() != null
                    && !ristoranteDescriptionI18n.getDescription().isEmpty()) {
                langpresent = true;
            }
        }
        return langpresent;
    }

    /**
     * Chek if the given risto has a not empty description on the given language
     * 
     * @param ristorante the risto to verify
     * @param language the language to check
     * @return true if the description is currently the
     */
    private boolean isTheCurrentDescriptionLanguage(Ristorante ristorante, Language language) {
        List<RistoranteDescriptionI18n> descs = ristorante.getDescriptions();
        boolean langpresent = false;
        for (RistoranteDescriptionI18n ristoranteDescriptionI18n : descs) {
            if (ristoranteDescriptionI18n.getLanguage().equals(language)) {
                langpresent = true;
            }
        }
        return langpresent;
    }
}
