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
import it.av.eatt.UserAlreadyExistsException;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.Language;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CountryService;
import it.av.eatt.service.EaterService;
import it.av.eatt.service.LanguageService;

import java.awt.Dimension;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.captcha.kittens.KittenCaptchaPanel;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * The panel provides the Sign Up form
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SignUpPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private Form<Eater> signUpForm;
    private FeedbackPanel feedbackPanel;
    private EaterService userService;
    private Link<String> goSignInAfterSignUp;
    private String passwordConfirm = "";
    private final KittenCaptchaPanel captcha;
    @SpringBean
    private LanguageService languageService;

    /**
     * Constructor
     * 
     * @param id
     * @param feedbackPanel
     * @param userService
     * @throws JackWicketException
     */
    public SignUpPanel(String id, FeedbackPanel feedbackPanel, EaterService userService, CountryService countryService)
            throws JackWicketException {
        super(id);
        this.userService = userService;
        this.feedbackPanel = feedbackPanel;

        RfcCompliantEmailAddressValidator emailAddressValidator = RfcCompliantEmailAddressValidator.getInstance();
        StringValidator pwdValidator = StringValidator.LengthBetweenValidator.lengthBetween(6, 20);

        Eater user = new Eater();
        signUpForm = new Form<Eater>("signUpForm", new CompoundPropertyModel<Eater>(user));

        signUpForm.setOutputMarkupId(true);
        signUpForm.add(new RequiredTextField<String>(Eater.FIRSTNAME));
        signUpForm.add(new RequiredTextField<String>(Eater.LASTNAME));
        signUpForm.add(new RequiredTextField<String>(Eater.EMAIL).add(emailAddressValidator));
        List<Country> countriyList = countryService.getAll();
        Country userCountry = null;
        for (Country country : countriyList) {
            if (country.getIso3().equals(getRequest().getLocale().getISO3Country())) {
                userCountry = country;
            }
        }
        DropDownChoice<Country> country = new DropDownChoice<Country>(Eater.COUNTRY, countryService.getAll());
        country.setRequired(true);
        country.setModel(new Model<Country>(userCountry));
        signUpForm.add(country);
        signUpForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()).setRequired(true));
        PasswordTextField pwd1 = new PasswordTextField(Eater.PASSWORD);
        pwd1.add(pwdValidator);
        signUpForm.add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model(passwordConfirm));
        signUpForm.add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        signUpForm.add(passwordInputValidator);
        SubmitButton submitButton = new SubmitButton("buttonCreateNewAccount", signUpForm);
        submitButton.setOutputMarkupId(true);
        add(submitButton);
        signUpForm.setDefaultButton(submitButton);

        goSignInAfterSignUp = new Link<String>("goSignInAfterSignUp") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SignIn.class);
            }
        };
        goSignInAfterSignUp.setOutputMarkupId(true);
        goSignInAfterSignUp.setVisible(false);
        add(goSignInAfterSignUp);

        signUpForm.add(captcha = new KittenCaptchaPanel("captcha", new Dimension(400, 200)));
        //used to show error message
        final HiddenField<String> captchaHidden = new HiddenField<String>("captchaHidden", new Model());
        signUpForm.add(captchaHidden);
        signUpForm.add(new KittenCaptchaValidator(captchaHidden));
        add(signUpForm);
    }

    private final class KittenCaptchaValidator extends AbstractFormValidator {
        private final HiddenField<String> captchaHidden;

        private KittenCaptchaValidator(HiddenField<String> captchaHidden) {
            this.captchaHidden = captchaHidden;
        }

        @Override
        public void validate(Form<?> form) {
            if(!captcha.allKittensSelected()){
                error(captchaHidden);
            }
        }

        @Override
        public FormComponent<?>[] getDependentFormComponents() {
            return new FormComponent[] { captchaHidden};
        }
    }

    private class SubmitButton extends AjaxButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Eater> form) {
            super(id, form);
        }
        
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                Eater user = (Eater) form.getModelObject();
                if (StringUtils.isNotBlank(user.getId())) {
                    getFeedbackPanel().info(
                            new StringResourceModel("info.operationNotPermitted", this, null).getString());
                } else {
                    System.out.println(target.getPage().getRequest().getLocale());
                    userService.addRegolarUser(user);
                    signUpForm.setVisible(false);
                    this.setVisible(false);
                    goSignInAfterSignUp.setVisible(true);
                }
            } catch (UserAlreadyExistsException e) {
                getFeedbackPanel().error(
                        new StringResourceModel("error.userAlreadyExistsException", this, null).getString());
            } catch (JackWicketException e) {
                getFeedbackPanel().error(
                        new StringResourceModel("error.operationNotPermitted", this, null).getString());
            }
            if(target !=null){
                target.addComponent(getPage());    
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form form) {
            getFeedbackPanel().anyErrorMessage();
            target.addComponent(getFeedbackPanel());
            target.addComponent(form);
        }
    }

    public final FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }
    
    private class LanguageRenderer implements IChoiceRenderer<Language>{
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