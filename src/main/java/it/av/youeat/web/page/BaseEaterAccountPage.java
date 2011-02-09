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
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CountryService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.LanguageService;
import it.av.youeat.web.components.ImageAvatarResource;
import it.av.youeat.web.components.ImagesAvatar;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation({ "USER", "ADMIN" })
public class BaseEaterAccountPage extends BasePage {

    @SpringBean
    private EaterService eaterService;
    @SpringBean
    private LanguageService languageService;
    @SpringBean
    private CountryService countryService;
    private Eater eater;
    private Image avatar;
    private Image avatarDefault;
    private WebMarkupContainer imagecontatiner;
    final private Form<Eater> accountForm;

    public BaseEaterAccountPage(PageParameters pageParameters) {
        if (!pageParameters.containsKey(YoueatHttpParams.YOUEAT_ID)) {
            throw new YoueatException("Missing user id");
        }
        String eaterId = pageParameters.getString(YoueatHttpParams.YOUEAT_ID, "");
        eater = eaterService.getByID(eaterId);

        accountForm = new Form<Eater>("account", new CompoundPropertyModel<Eater>(eater));
        accountForm.setOutputMarkupId(true);
        add(accountForm);
        accountForm.add(new RequiredTextField<String>("firstname"));
        accountForm.add(new RequiredTextField<String>("lastname"));
        DropDownChoice<Country> country = new DropDownChoice<Country>(Eater.COUNTRY, countryService.getAll());
        country.setRequired(true);
        accountForm.add(country);
        accountForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()));

        Form formAvatar = new Form("formAvatar");
        add(formAvatar);
        formAvatar.setOutputMarkupId(true);
        formAvatar.setMultiPart(true);
        formAvatar.setMaxSize(Bytes.megabytes(1));
        FileUploadField uploadField = new FileUploadField("uploadField");
        formAvatar.add(uploadField);
        // formAvatar.add(new UploadProgressBar("progressBar", accountForm));
        formAvatar.add(new SubmitAvatarButton("submitForm", formAvatar));
        imagecontatiner = new WebMarkupContainer("imageContainer");
        imagecontatiner.setOutputMarkupId(true);
        formAvatar.add(imagecontatiner);

        avatar = new NonCachingImage("avatar", new ImageAvatarResource(eater));
        avatar.setOutputMarkupPlaceholderTag(true);
        imagecontatiner.add(avatar);
        avatarDefault = ImagesAvatar.getAvatar("avatarDefault", eater, this, true);
        if (eater.getAvatar() != null) {
            avatarDefault.setVisible(false);
        } else {
            avatar.setVisible(false);
        }
        imagecontatiner.add(avatarDefault);

    }

    private class SubmitAvatarButton extends AjaxFallbackButton {

        public SubmitAvatarButton(String id, Form<Eater> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            FileUpload upload = ((FileUploadField) form.get("uploadField")).getFileUpload();
            if (upload != null) {
                accountForm.getModelObject().setAvatar(upload.getBytes());
                try {
                    eaterService.update(accountForm.getModelObject());
                    eater = eaterService.getByID(eater.getId());
                    ((CompoundPropertyModel<Eater>) accountForm.getModel()).setObject(eater);
                    getFeedbackPanel().info("picture changed");
                } catch (YoueatException e) {
                    getFeedbackPanel().error(getString("genericErrorMessage"));
                }
            }
            // it's necessary to remove the initial reference to default avatar
            // ImagesAvatar imagesAvatar = new ImagesAvatar();
            // avatar = imagesAvatar.getAvatar(avatar.getId(), eater, this.getPage(), false);
            avatar.setImageResource(new ImageAvatarResource(eater));
            avatar.setVisible(true);
            avatarDefault.setVisible(false);
            // avatar = new NonCachingImage(avatar.getId(), new ThumbnailImageResource(new ByteArrayResource("image/png",
            // refreshedEater.getAvatar()), 100));
            if (target != null) {
                target.addComponent(getFeedbackPanel());
                target.addComponent(imagecontatiner);
                target.addComponent(avatar);
            }
        }
    }

    protected class LanguageRenderer implements IChoiceRenderer<Language> {
        @Override
        public Object getDisplayValue(Language object) {
            return getString(object.getLanguage());
        }

        @Override
        public String getIdValue(Language object, int index) {
            return object.getId();
        }
    }

    public Form<Eater> getAccountForm() {
        return accountForm;
    }

    /**
     * @return the eater
     */
    public Eater getEater() {
        return eater;
    }

    /**
     * @return the avatar
     */
    public Image getAvatar() {
        return avatar;
    }

    /**
     * @param eater the eater to set
     */
    protected void setEater(Eater eater) {
        this.eater = eater;
    }

    /**
     * @return the eaterService
     */
    protected EaterService getEaterService() {
        return eaterService;
    }

}