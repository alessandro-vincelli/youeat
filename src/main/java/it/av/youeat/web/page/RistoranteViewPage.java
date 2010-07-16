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
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Comment;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistoranteDescriptionI18n;
import it.av.youeat.ocm.model.RistorantePicture;
import it.av.youeat.ocm.model.Tag;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.LanguageService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.Locales;
import it.av.youeat.web.components.FriendEaterListView;
import it.av.youeat.web.components.ImageRisto;
import it.av.youeat.web.panel.RistoranteRevisionsPanel;
import it.av.youeat.web.util.DefaultFocusBehavior;
import it.av.youeat.web.util.RistoranteUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.springframework.util.Assert;

import wicket.contrib.gmap.GMap2;
import wicket.contrib.gmap.GMapHeaderContributor;
import wicket.contrib.gmap.api.GControl;
import wicket.contrib.gmap.api.GEvent;
import wicket.contrib.gmap.api.GEventHandler;
import wicket.contrib.gmap.api.GLatLng;
import wicket.contrib.gmap.api.GMapType;
import wicket.contrib.gmap.api.GMarker;
import wicket.contrib.gmap.api.GMarkerOptions;

/**
 * The page shows all the {@link Ristorante} informations.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteViewPage extends BasePage {

    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean
    private LanguageService languageService;
    @SpringBean
    private ActivityRistoranteService activityService;

    private Ristorante ristorante = new Ristorante();;

    private ModalWindow revisionModal;
    private boolean hasVoted = Boolean.FALSE;
    private Language actualDescriptionLanguage;
    private ListView<RistoranteDescriptionI18n> descriptions;
    private WebMarkupContainer descriptionsContainer;
    private Form<Ristorante> formRisto;
    private WebMarkupContainer descriptionLinksContainer;
    private ListView<RistorantePicture> picturesList;
    private Label asfavouriteLabel;
    private Form<Comment> formComment;
    private ListView<Comment> commentsList;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public RistoranteViewPage(PageParameters parameters) throws YoueatException {
        actualDescriptionLanguage = getInitialLanguage();
        String ristoranteId = parameters.getString(YoueatHttpParams.RISTORANTE_ID, "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {
            throw new RestartResponseAtInterceptPageException(getApplication().getHomePage());
        }
        ristorante = ristorante.addDescLangIfNotPresent(actualDescriptionLanguage);
        formRisto = new Form<Ristorante>("ristoranteForm", new CompoundPropertyModel<Ristorante>(ristorante));
        add(formRisto);
        formRisto.setOutputMarkupId(true);
        formRisto.add(new Label(Ristorante.NAME));

        formRisto.add(new SmartLinkLabel(Ristorante.WWW));
        formRisto.add(new SmartLinkLabel(Ristorante.EMAIL));
        formRisto.add(new ListView<Tag>(Ristorante.TAGS) {

            @Override
            protected void populateItem(ListItem<Tag> item) {
                StringBuffer tag = new StringBuffer(item.getModelObject().getTag());
                if(item.getIndex() < ristorante.getTags().size() - 1){
                    tag.append(",");
                }
                item.add(new Label("tagItem", tag.toString()));
            }
        });
        descriptionLinksContainer = new WebMarkupContainer("descriptionLinksContainer");
        descriptionLinksContainer.setOutputMarkupId(true);
        formRisto.add(descriptionLinksContainer);
        ListView<Language> descriptionsLinks = new ListView<Language>("descriptionLinks", languageService.getAll()) {
            @Override
            protected void populateItem(final ListItem<Language> item) {
                item.add(new AjaxFallbackButton("descriptionLink", formRisto) {

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        boolean langpresent = ristorante.checkDesctiption(item.getModelObject());
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
                        actualDescriptionLanguage = item.getModelObject();
                        ristorante = ristorante.addDescLangIfNotPresent(actualDescriptionLanguage);
                        formRisto.setModelObject(ristorante);
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
        formRisto.add(descriptionsContainer);
        descriptions = new ListView<RistoranteDescriptionI18n>("descriptions", new DescriptionsModel()) {
            @Override
            protected void populateItem(ListItem<RistoranteDescriptionI18n> item) {
                boolean visible = actualDescriptionLanguage.equals(item.getModelObject().getLanguage());
                if (item.getModelObject().getDescription() == null || item.getModelObject().getDescription().isEmpty()) {
                    item.add(new Label(RistoranteDescriptionI18n.DESCRIPTION, getString("descriptionNotAvailableLang"))
                            .setVisible(visible).setOutputMarkupPlaceholderTag(true));
                } else {
                    item.add(new MultiLineLabel(RistoranteDescriptionI18n.DESCRIPTION, new PropertyModel<String>(item
                            .getModelObject(), RistoranteDescriptionI18n.DESCRIPTION)).setVisible(visible)
                            .setOutputMarkupPlaceholderTag(true));
                }
            }
        };
        descriptionsContainer.add(descriptions);
        formRisto.add(new Label("revisionNumber"));

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
                setHasVoted(Boolean.TRUE);
                ristoranteService.addRate(getRistorante(), getLoggedInUser(), rating);
                ristorante = ristoranteService.getByID(ristorante.getId());
                info(getString("info.ratingSaved"));
                if (target != null) {
                    target.addComponent(getFeedbackPanel());
                }
            }
        });
        BookmarkablePageLink editAddressRistorante = new BookmarkablePageLink("editAddressRistorante",
                RistoranteEditAddressPage.class, new PageParameters(YoueatHttpParams.RISTORANTE_ID + "="
                        + ristorante.getId()));
        editAddressRistorante.setOutputMarkupId(true);
        add(editAddressRistorante);

        if (getApplication().getSecuritySettings().getAuthorizationStrategy().isInstantiationAuthorized(
                RistoranteEditAddressPage.class)) {
            editAddressRistorante.setVisible(true);
        } else {
            editAddressRistorante.setVisible(false);
        }
        add(editAddressRistorante);

        BookmarkablePageLink editDataRistorante = new BookmarkablePageLink("editDataRistorante",
                RistoranteEditDataPage.class, new PageParameters(YoueatHttpParams.RISTORANTE_ID + "="
                        + ristorante.getId()));
        editDataRistorante.setOutputMarkupId(true);
        add(editDataRistorante);

        BookmarkablePageLink editPictures = new BookmarkablePageLink("editPictures", RistoranteEditPicturePage.class,
                new PageParameters(YoueatHttpParams.RISTORANTE_ID + "=" + ristorante.getId()));
        editPictures.setOutputMarkupId(true);
        add(editPictures);

        picturesList = new ListView<RistorantePicture>("picturesList", ristorante.getActivePictures()) {

            @Override
            protected void populateItem(final ListItem<RistorantePicture> item) {
                Link<RistorantePicture> imageLink = new Link<RistorantePicture>("pictureLink", item.getModel()) {
                    @Override
                    public void onClick() {
                        setResponsePage(new ImageViewPage(getModelObject().getPicture()));
                    }
                };
                imageLink.add(ImageRisto.getThumbnailImage("picture", item.getModelObject().getPicture(), true));
                item.add(imageLink);
            }
        };
        formRisto.add(picturesList);

        add(revisionModal = new ModalWindow("revisionsPanel"));
        revisionModal.setWidthUnit("%");
        revisionModal.setInitialHeight(450);
        revisionModal.setInitialWidth(100);
        revisionModal.setResizable(false);
        RistoranteRevisionsPanel revisionsPanel = new RistoranteRevisionsPanel(revisionModal.getContentId(),
                getFeedbackPanel());
        revisionsPanel.refreshRevisionsList(ristorante, actualDescriptionLanguage);
        revisionModal.setContent(revisionsPanel);
        revisionModal.setTitle("Revisions list");
        revisionModal.setCookieName("SC-revisionLists");

        revisionModal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                return true;
            }
        });

        revisionModal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {

            }
        });

        add(new AjaxLink("showsAllRevisions") {
            public void onClick(AjaxRequestTarget target) {
                revisionModal.show(target);
            }
        });

        add(new AjaxLink<String>("tried") {
            public void onClick(AjaxRequestTarget target) {
                if (getLoggedInUser() != null) {
                    activityService.addTriedRisto(getLoggedInUser(), ristorante);
                    info(getString("info.IateHere", new Model<Ristorante>(ristorante)));
                } else {
                    info(getString("action.notlogged"));
                }
                target.addComponent(getFeedbackPanel());
            }
        });

        AjaxFallbackLink<String> asfavourite = new AjaxFallbackLink<String>("asfavourite") {
            public void onClick(AjaxRequestTarget target) {
                if (getLoggedInUser() != null) {
                    if (activityService.isFavouriteRisto(getLoggedInUser(), ristorante)) {
                        activityService.addRistoAsFavorite(getLoggedInUser(), ristorante);
                        info(getString("action.removedAsFavourite", new Model<Ristorante>(ristorante)));
                        asfavouriteLabel.setDefaultModelObject(getString("button.addAsFavourite"));
                    } else {
                        activityService.save(new ActivityRistorante(getLoggedInUser(), ristorante,
                                ActivityRistorante.TYPE_ADDED_AS_FAVOURITE));
                        info(getString("action.addedAsFavourite", new Model<Ristorante>(ristorante)));
                        asfavouriteLabel.setDefaultModelObject(getString("button.removeAsFavourite"));
                    }
                } else {
                    info(getString("action.notlogged"));
                }
                if (target != null) {
                    target.addComponent(asfavouriteLabel);
                    target.addComponent(getFeedbackPanel());
                }
            }
        };
        add(asfavourite);

        formComment = new Form<Comment>("formComment", new CompoundPropertyModel<Comment>(new Comment()));
        formComment.setOutputMarkupId(true);
        add(formComment);
        final TextField<String> newCommentTitle = new TextField<String>(Comment.TITLE_FIELD);
        newCommentTitle.setVisible(false);
        newCommentTitle.add(StringValidator.maximumLength(Comment.TITLE_MAX_LENGTH));
        newCommentTitle.setOutputMarkupPlaceholderTag(true);
        formComment.add(newCommentTitle);
        final TextArea<String> newCommentBody = new TextArea<String>(Comment.BODY_FIELD);
        newCommentBody.setRequired(true);
        newCommentBody.add(StringValidator.maximumLength(Comment.BODY_MAX_LENGTH));
        newCommentBody.setVisible(false);
        newCommentBody.setOutputMarkupPlaceholderTag(true);
        formComment.add(newCommentBody);
        final Label newCommentTitleLabel = new Label("newCommentTitleLabel", getString("label.newCommentTitle"));
        newCommentTitleLabel.setOutputMarkupPlaceholderTag(true);
        newCommentTitleLabel.setVisible(false);
        formComment.add(newCommentTitleLabel);
        final AjaxFallbackButton submitNewComment = new AjaxFallbackButton("submitNewComment", formComment) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (getLoggedInUser() != null) {
                    Comment comment = (Comment) getForm().getModelObject();
                    comment.setAuthor(getLoggedInUser());
                    comment.setCreationTime(DateUtil.getTimestamp());
                    ristorante.addComment(comment);
                    try {
                        ristoranteService.updateNoRevision(ristorante);
                        activityService.save(new ActivityRistorante(getLoggedInUser(), ristorante,
                                ActivityRistorante.TYPE_NEW_COMMENT));
                        // reset the new comment formRisto
                        formComment.setModelObject(new Comment());
                        newCommentBody.setVisible(false);
                        newCommentTitle.setVisible(false);
                        newCommentTitleLabel.setVisible(false);
                        this.setVisible(false);
                        newCommentTitleLabel.setVisible(false);
                        Comment commeTitleToShow = new Comment();
                        commeTitleToShow.setTitle(comment.getTitle() != null ? "'" + comment.getTitle() + "'" : "");
                        info(getString("message.newCommentAdded", new Model<Comment>(commeTitleToShow)));
                        if (target != null) {
                            target.addComponent(formComment);
                        }
                    } catch (YoueatException e) {
                        getFeedbackPanel().error(getString("genericErrorMessage"));
                    }
                    if (target != null) {
                        target.addComponent(getFeedbackPanel());
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if (target != null) {
                    target.addComponent(getFeedbackPanel());
                }
            }
        };
        submitNewComment.setOutputMarkupPlaceholderTag(true);
        submitNewComment.setVisible(false);
        formComment.add(submitNewComment);
        commentsList = new ListView<Comment>("commentsList", new CommentsModel()) {

            @Override
            protected void populateItem(final ListItem<Comment> item) {
                item.add(new Label(Comment.AUTHOR_FIELD, item.getModelObject().getAuthor().getFirstname() + " "
                        + item.getModelObject().getAuthor().getLastname()));
                item.add(new Label(Comment.TITLE_FIELD, item.getModelObject().getTitle()));
                item.add(new Label(Comment.CREATIONTIME_FIELD, DateUtil.SDF2SHOW.print(item.getModelObject()
                        .getCreationTime().getTime())));
                item.add(new MultiLineLabel(Comment.BODY_FIELD, item.getModelObject().getBody()));
            }
        };
        formComment.add(commentsList);
        add(new NewCommentButton("newComment", newCommentBody, submitNewComment, newCommentTitleLabel, newCommentTitle));
        add(new NewCommentButton("newCommentBottom", newCommentBody, submitNewComment, newCommentTitleLabel,
                newCommentTitle));
        asfavouriteLabel = new Label("asfavouriteLabel", getString("button.addAsFavourite"));
        if (activityService.isFavouriteRisto(getLoggedInUser(), ristorante)) {
            asfavouriteLabel.setDefaultModelObject(getString("button.removeAsFavourite"));
        }
        asfavouriteLabel.setOutputMarkupId(true);
        asfavourite.add(asfavouriteLabel);

        setHasVoted(ristoranteService.hasUsersAlreadyRated(getRistorante(), getLoggedInUser())
                || getLoggedInUser() == null);

        // position on the map
        String gmapKey = "ABQIAAAAEpqZyWLxrLSc1icxiiTLyBRjFP5Ion2TodTauLHyn40LiCPQaRSoBSldN1pDUDTAPEK5AlXpouSLuA";
        final GMap2 bottomMap = new GMap2("map", new GMapHeaderContributor(gmapKey));
        bottomMap.setOutputMarkupId(true);
        bottomMap.setMapType(GMapType.G_NORMAL_MAP);
        bottomMap.addControl(GControl.GSmallMapControl);
        bottomMap.addControl(GControl.GMapTypeControl);
        bottomMap.setZoom(20);
        if (ristorante.getLatitude() != 0 && ristorante.getLongitude() != 0) {
            GLatLng gLatLng = new GLatLng(ristorante.getLatitude(), ristorante.getLongitude());
            GMarkerOptions markerOptions = new GMarkerOptions(ristorante.getName());
            markerOptions = markerOptions.draggable(true);
            GMarker marker = new GMarker(gLatLng, markerOptions){
                @Override
                protected void updateOnAjaxCall(AjaxRequestTarget target, GEvent overlayEvent) {
                    super.updateOnAjaxCall(target, overlayEvent);
                    if(getLoggedInUser() != null){
                        ristorante.setLatitude(this.getLatLng().getLat());
                        ristorante.setLongitude(this.getLatLng().getLng());
                        ristorante = ristoranteService.updateLatitudeLongitude(ristorante);    
                    }
                }
            };
            marker.addListener(GEvent.dragend, new GEventHandler() {
                
                @Override
                public void onEvent(AjaxRequestTarget target) {
                    //attach the ajax code to handle the event 
                }
            });
            bottomMap.addOverlay(marker);
            bottomMap.setCenter(gLatLng);
        } else {
            bottomMap.setVisible(false);
        }
        add(bottomMap);
        // users that already tried infos
        List<ActivityRistorante> friendThatAlreadyEat = new ArrayList<ActivityRistorante>(0);
        int numberUsersThatAlreadyEat = 0;

        if (getLoggedInUser() != null) {
            friendThatAlreadyEat = activityService.findByFriendWithActivitiesOnRistorante(getLoggedInUser(),
                    ristorante, ActivityRistorante.TYPE_TRIED);
            numberUsersThatAlreadyEat = activityService.countByRistoAndType(ristorante, ActivityRistorante.TYPE_TRIED);
        }
        add(new Label("friendEaterListTitle", getString("numberOfUsersAlreadyEatAt", new Model<NumberBean>(
                new NumberBean(numberUsersThatAlreadyEat)))).setVisible(numberUsersThatAlreadyEat > 0));
        add(new FriendEaterListView("friendEaterList", friendThatAlreadyEat).setVisible(friendThatAlreadyEat.size() > 0));

        // contribution infos
        List<ActivityRistorante> friendContributions = new ArrayList<ActivityRistorante>(0);
        int numberOfContributions = 0;

        if (getLoggedInUser() != null) {
            friendContributions = activityService.findByFriendContributionsOnRistorante(getLoggedInUser(), ristorante);
            numberOfContributions = activityService.countContributionsOnRistorante(ristorante);
        }
        add(new Label("numberOfContributions", getString("numberOfContributions", new Model<NumberBean>(new NumberBean(
                numberOfContributions)))).setVisible(numberOfContributions > 0));
        add(new FriendEaterListView("friendContributionsList", friendContributions).setVisible(friendContributions
                .size() > 0));
    }

    public RistoranteViewPage(Ristorante ristorante) throws YoueatException {
        this(RistoranteUtil.createParamsForRisto(ristorante));
    }

    public final Ristorante getRistorante() {
        return ristorante;
    }

    public final void setRistorante(Ristorante ristorante) {
        this.ristorante = ristorante;
    }

    /**
     * @param hasVoted the hasVoted to set
     */
    private void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
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

    private final class NewCommentButton extends AjaxFallbackLink<String> {
        private final TextArea<String> newCommentBody;
        private final AjaxFallbackButton submitNewComment;
        private final Label newCommentTitleLabel;
        private final TextField<String> newCommentTitle;

        private NewCommentButton(String id, TextArea<String> newCommentBody, AjaxFallbackButton submitNewComment,
                Label newCommentTitleLabel, TextField<String> newCommentTitle) {
            super(id);
            this.newCommentBody = newCommentBody;
            this.submitNewComment = submitNewComment;
            this.newCommentTitleLabel = newCommentTitleLabel;
            this.newCommentTitle = newCommentTitle;
        }

        public void onClick(AjaxRequestTarget target) {
            // comment allowed only for logged user
            if (getLoggedInUser() != null) {
                newCommentBody.setVisible(true);
                submitNewComment.setVisible(true);
                newCommentTitle.setVisible(true);
                newCommentTitleLabel.setVisible(true);
                formComment.get("title").add(new DefaultFocusBehavior());
                if (target != null) {
                    target.addComponent(formComment);
                }
            } else {
                if (target != null) {
                    info(getString("basePage.notLogged"));
                }
            }
            if (target != null) {
                target.addComponent(getFeedbackPanel());
            }
        }
    }

    private class CommentsModel extends LoadableDetachableModel<List<Comment>> {
        public CommentsModel() {
            super();
        }

        public CommentsModel(List<Comment> comments) {
            super(comments);
        }

        @Override
        protected List<Comment> load() {
            ristorante = ristoranteService.getByID(ristorante.getId());
            return ristorante.getComments();
        }
    }

    private class DescriptionsModel extends LoadableDetachableModel<List<RistoranteDescriptionI18n>> {
        public DescriptionsModel() {
            super();
        }

        public DescriptionsModel(List<RistoranteDescriptionI18n> descs) {
            super(descs);
        }

        @Override
        protected List<RistoranteDescriptionI18n> load() {
            return ristorante.getDescriptions();
        }
    }

    private class NumberBean implements Serializable {
        private int number = 0;

        public NumberBean(int number) {
            super();
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
