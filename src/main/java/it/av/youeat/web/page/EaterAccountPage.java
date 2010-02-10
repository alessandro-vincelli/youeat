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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.service.EaterService;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class EaterAccountPage extends BaseEaterAccountPage {

    @SpringBean
    private EaterService eaterService;
    private String confirmPassword = "";
    private String oldPasswordValue = "";
    private String newPasswordValue = "";

    public EaterAccountPage(PageParameters pageParameters) {
        super(pageParameters);
        getAccountForm().add(new Label("email"));
        StringValidator pwdValidator = StringValidator.LengthBetweenValidator.lengthBetween(6, 20);
        PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>(oldPasswordValue));
        oldPassword.add(new OldPasswordValidator(getAccountForm()));
        oldPassword.setEnabled(!getLoggedInUser().isSocialNetworkEater());
        getAccountForm().add(oldPassword);
        PasswordTextField pwd1 = new PasswordTextField("newPassword", new Model<String>(newPasswordValue));
        pwd1.setRequired(false);
        pwd1.setEnabled(!getLoggedInUser().isSocialNetworkEater());
        pwd1.add(pwdValidator);
        pwd1.setResetPassword(false);
        getAccountForm().add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(confirmPassword));
        pwd2.setRequired(false);
        pwd2.setEnabled(!getLoggedInUser().isSocialNetworkEater());
        getAccountForm().add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        getAccountForm().add(passwordInputValidator);
        getAccountForm().add(new SubmitButton("saveAccount", getAccountForm()));

    }

    private class SubmitButton extends AjaxFallbackButton {
        public SubmitButton(String id, Form<Eater> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            info(getString("info.accountSaved"));
            String newPwd = form.get("password-confirm").getDefaultModelObjectAsString();
            if ((!newPwd.isEmpty())) {
                ((Eater)form.getModelObject()).setPassword(eaterService.encodePassword(newPwd, ((Eater)form.getModelObject()).getPasswordSalt()));
            }
            Eater eater = (Eater) form.getModelObject();
            getEaterService().update(eater);
            ((CompoundPropertyModel<Eater>)form.getModel()).setObject(getEaterService().getByID(eater.getId()));
            newPasswordValue = "";
            oldPasswordValue = "";
            confirmPassword = "";
            if (target != null) {
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

    private class OldPasswordValidator extends AbstractValidator<String> {
        Form<Eater> form;
        public OldPasswordValidator(Form<Eater> form) {
            super();
            this.form = form;
        }

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            if (!eaterService.isPasswordValid(form.getModelObject().getPassword(), validatable.getValue().toString(), form.getModelObject()
                    .getPasswordSalt())) {
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