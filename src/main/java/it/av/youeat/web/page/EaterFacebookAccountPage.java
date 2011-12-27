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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class EaterFacebookAccountPage extends BaseEaterAccountPage {

    public EaterFacebookAccountPage(PageParameters pageParameters) {
        super(pageParameters);
        getAccountForm().add(new SubmitButton("saveAccount", getAccountForm()));
    }

    private class SubmitButton extends AjaxFallbackButton {
        public SubmitButton(String id, Form<Eater> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            info(getString("info.accountSaved"));
            Eater eater = (Eater) form.getModelObject();
            getEaterService().update(eater);
            ((CompoundPropertyModel<Eater>)form.getModel()).setObject(getEaterService().getByID(eater.getId()));
            if (target != null) {
                target.addComponent(getFeedbackPanel());
                target.addComponent(form);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            //super.onError(target, form);
            //TODO 1.5
            target.addComponent(getFeedbackPanel());
        }
    }
}