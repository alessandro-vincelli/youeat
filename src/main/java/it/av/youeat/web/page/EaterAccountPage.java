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
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CountryService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.LanguageService;
import it.av.youeat.web.components.ImageAvatarResource;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class EaterAccountPage extends BasePage {
    
    @SpringBean
    private EaterService eaterService;
    @SpringBean
    private LanguageService languageService;
    @SpringBean
    private CountryService countryService; 
    private String confirmPassword = "";
    private String oldPasswordValue = "";
    private String newPasswordValue = "";
    private Eater eater;
    private Image avatar;
    private WebMarkupContainer imagecontatiner;

    public EaterAccountPage(PageParameters pageParameters){
        if (!pageParameters.containsKey(YoueatHttpParams.YOUEAT_ID)) {
            throw new YoueatException("Missing user id");
        }
        String eaterId = pageParameters.getString(YoueatHttpParams.YOUEAT_ID, "");
        StringValidator pwdValidator = StringValidator.LengthBetweenValidator.lengthBetween(6, 20);
        eater = eaterService.getByID(eaterId);

        final Form<Eater> accountForm = new Form<Eater>("account", new CompoundPropertyModel<Eater>(eater));
        accountForm.setOutputMarkupId(true);
        add(accountForm);
        accountForm.add(new Label("email"));
        accountForm.add(new RequiredTextField<String>("firstname"));
        accountForm.add(new RequiredTextField<String>("lastname"));
        DropDownChoice<Country> country = new DropDownChoice<Country>(Eater.COUNTRY, countryService.getAll());
        country.setRequired(true);
        accountForm.add(country);
        accountForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()));
        PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>(oldPasswordValue));
        oldPassword.add(new OldPasswordValidator());
        oldPassword.setEnabled(!eater.isSocialNetworkEater());
        accountForm.add(oldPassword);
        PasswordTextField pwd1 = new PasswordTextField("newPassword", new Model<String>(newPasswordValue));
        pwd1.setRequired(false);
        pwd1.setEnabled(!eater.isSocialNetworkEater());
        pwd1.add(pwdValidator);
        pwd1.setResetPassword(false);
        accountForm.add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(confirmPassword));
        pwd2.setRequired(false);
        pwd2.setEnabled(!eater.isSocialNetworkEater());
        accountForm.add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        accountForm.add(passwordInputValidator);
        accountForm.add(new SubmitButton("saveAccount", accountForm).setVisible(!eater.isSocialNetworkEater()));

        Form formAvatar = new Form("formAvatar");
        add(formAvatar);
        formAvatar.setOutputMarkupId(true);
        formAvatar.setMultiPart(true);
        formAvatar.setMaxSize(Bytes.megabytes(1));
        FileUploadField uploadField = new FileUploadField("uploadField");
        formAvatar.add(uploadField);
        formAvatar.add(new UploadProgressBar("progressBar", accountForm));
        formAvatar.add(new SubmitAvatarButton("submitForm", formAvatar));
        imagecontatiner = new WebMarkupContainer("imageContainer");
        imagecontatiner.setOutputMarkupId(true);
        formAvatar.add(imagecontatiner);
        avatar = new NonCachingImage("avatar", new ImageAvatarResource(eater));
        imagecontatiner.add(avatar);
    }

    private class SubmitButton extends AjaxFallbackButton {
        public SubmitButton(String id, Form<Eater> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            info(getString("info.accountSaved"));
            String newPwd = form.get("password-confirm").getDefaultModelObjectAsString();
            if ((!newPwd.isEmpty())) {
                eater.setPassword(eaterService.encodePassword(newPwd, eater.getPasswordSalt()));
            }
            eaterService.update((Eater) form.getModelObject());
            eater = eaterService.getByID(eater.getId());
            form.setDefaultModelObject(eater);
            newPasswordValue = "";
            oldPasswordValue = "";
            confirmPassword = "";
            if(target !=null){
                target.addComponent(getFeedbackPanel());
                target.addComponent(form);    
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            super.onError(target, form);
            target.addComponent(getFeedbackPanel());
        }
    }

    private class SubmitAvatarButton extends AjaxFallbackButton {

        public SubmitAvatarButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            FileUpload upload = ((FileUploadField) form.get("uploadField")).getFileUpload();
            Eater refreshedEater = new Eater();
            if (upload != null) {
                eater.setAvatar(upload.getBytes());
                try {
                    eaterService.update(eater);
                    eater = eaterService.getByID(eater.getId());
                    refreshedEater = eaterService.getByID(eater.getId());
                    getFeedbackPanel().info("picture changed");
                } catch (YoueatException e) {
                    getFeedbackPanel().error(getString("genericErrorMessage"));
                }
            }
            //it's necessary to remove the initial reference to default avatar
            //ImagesAvatar imagesAvatar = new ImagesAvatar();
            //avatar = imagesAvatar.getAvatar(avatar.getId(), eater, this.getPage(), false);
            avatar = new NonCachingImage(avatar.getId(), new ImageAvatarResource(eater));
            //avatar = new NonCachingImage(avatar.getId(), new ThumbnailImageResource(new ByteArrayResource("image/png", refreshedEater.getAvatar()), 100));
            if (target != null) {
                target.addComponent((form.get("imageContainer")));
                target.addComponent(getFeedbackPanel());
                target.addComponent(imagecontatiner);
            }
        }
    }

    private class OldPasswordValidator extends AbstractValidator<String> {

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            if (!eaterService.isPasswordValid(eater.getPassword(), validatable.getValue().toString(), eater.getPasswordSalt())) {
                error(validatable);
            }
        }
    }

    private class LanguageRenderer implements IChoiceRenderer<Language> {
        @Override
        public Object getDisplayValue(Language object) {
            return getString(object.getLanguage());
        }

        @Override
        public String getIdValue(Language object, int index) {
            return object.getId();
        }
    }

}