package it.av.youeat.web.components;

import org.apache.wicket.extensions.captcha.kittens.KittenCaptchaPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

public class KittenCaptchaValidator extends AbstractFormValidator {
    private final HiddenField<String> captchaHidden;
    private final KittenCaptchaPanel captcha;

    /**
     * @param captchaHidden
     * @param captcha
     */
    public KittenCaptchaValidator(HiddenField<String> captchaHidden, KittenCaptchaPanel captcha) {
        super();
        this.captchaHidden = captchaHidden;
        this.captcha = captcha;
    }

    /**
     * @return the captchaHidden
     */
    public HiddenField<String> getCaptchaHidden() {
        return captchaHidden;
    }

    /**
     * @return the captcha
     */
    public KittenCaptchaPanel getCaptcha() {
        return captcha;
    }

    @Override
    public void validate(Form<?> form) {
        if (!captcha.allKittensSelected()) {
            error(captchaHidden);
        }
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return new FormComponent[] { captchaHidden };
    }
}