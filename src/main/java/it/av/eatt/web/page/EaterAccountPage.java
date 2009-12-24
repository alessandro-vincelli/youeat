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
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.Language;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.service.EaterService;
import it.av.eatt.service.LanguageService;
import it.av.eatt.web.commons.ImagesCommons;

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
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER" })
public class EaterAccountPage extends BasePage {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterService eaterService;
    @SpringBean
    private LanguageService languageService;
    private String confirmPassword = "";
    private String oldPasswordValue = "";
    private String newPasswordValue = "";
    private Eater eater;
    private Image avatar;

    public EaterAccountPage(PageParameters pageParameters) throws JackWicketException {
        if (!pageParameters.containsKey(YoueatHttpParams.PARAM_YOUEAT_ID)) {
            throw new JackWicketException("Missing user id");
        }
        String eaterId = pageParameters.getString(YoueatHttpParams.PARAM_YOUEAT_ID, "");
        StringValidator pwdValidator = StringValidator.LengthBetweenValidator.lengthBetween(6, 20);
        eater = eaterService.getByID(eaterId);

        Form<Eater> form = new Form<Eater>("account", new CompoundPropertyModel<Eater>(eater));
        form.setOutputMarkupId(true);
        add(form);
        form.add(new Label("email"));
        form.add(new RequiredTextField<String>("firstname"));
        form.add(new RequiredTextField<String>("lastname"));
        form.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()));
        PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>(oldPasswordValue));
        oldPassword.add(new OldPasswordValidator());
        form.add(oldPassword);
        PasswordTextField pwd1 = new PasswordTextField("newPassword", new Model<String>(newPasswordValue));
        pwd1.setRequired(false);
        pwd1.add(pwdValidator);
        pwd1.setResetPassword(false);
        form.add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(confirmPassword));
        pwd2.setRequired(false);
        form.add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        form.add(passwordInputValidator);
        form.add(new SubmitButton("saveAccount", form));

        Form formAvatar = new Form("formAvatar");
        add(formAvatar);
        formAvatar.setOutputMarkupId(true);
        formAvatar.setMultiPart(true);
        formAvatar.setMaxSize(Bytes.megabytes(1));
        FileUploadField uploadField = new FileUploadField("uploadField");
        formAvatar.add(uploadField);
        formAvatar.add(new UploadProgressBar("progressBar", form));
        formAvatar.add(new SubmitAvatarButton("submitForm", formAvatar));
        WebMarkupContainer imagecontatiner = new WebMarkupContainer("imageContainer");
        imagecontatiner.setOutputMarkupId(true);
        formAvatar.add(imagecontatiner);
        avatar = ImagesCommons.getAvatar("avatar", eater, this.getPage(), false);
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
                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                eater.setPassword(passwordEncryptor.encryptPassword(newPwd));
            }
            eaterService.update((Eater) form.getModelObject());
            newPasswordValue = "";
            oldPasswordValue = "";
            confirmPassword = "";
            target.addComponent(getFeedbackPanel());
            target.addComponent(form);
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            super.onError(target, form);
            target.addComponent(getFeedbackPanel());
        }
    }

    private class SubmitAvatarButton extends AjaxFallbackButton {
        private static final long serialVersionUID = 1L;

        public SubmitAvatarButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            FileUpload upload = ((FileUploadField) form.get("uploadField")).getFileUpload();
            if (upload != null) {
                eater.setAvatar(upload.getBytes());
                try {
                    eaterService.update(eater);
                    eater = eaterService.getByID(eater.getId());
                    getFeedbackPanel().info("picture changed");
                } catch (JackWicketException e) {
                    getFeedbackPanel().error(getString("An error occurred"));
                }
            }
            avatar.setImageResource(new DynamicImageResource() {
                @Override
                protected byte[] getImageData() {
                    return eater.getAvatar();
                }
            });
            if (target != null) {
                target.addComponent((form.get("imageContainer")));
                target.addComponent(getFeedbackPanel());
            }
        }
    }

    private class OldPasswordValidator extends AbstractValidator<String> {

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            if (!passwordEncryptor.checkPassword(validatable.getValue().toString(), eater.getPassword())) {
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