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
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.RistorantePicture;
import it.av.eatt.service.RistorantePictureService;
import it.av.eatt.service.RistoranteService;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

/**
 * Add and Remove picture of {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN", "EDITOR" })
public class RistoranteEditPicturePage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean
    private RistorantePictureService ristorantePictureService;

    private Ristorante ristorante;
    private Form<Ristorante> form;
    private FileUploadField uploadField;
    private ListView<RistorantePicture> picturesList;

    /**
     * @param ristorante
     * @throws JackWicketException
     */
    public RistoranteEditPicturePage(Ristorante ristorante) throws JackWicketException {
        this(new PageParameters("ristoranteId=" + ristorante.getId()));
    }

    /**
     * @param parameters
     * @throws JackWicketException
     */
    public RistoranteEditPicturePage(PageParameters parameters) throws JackWicketException {

        String ristoranteId = parameters.getString("ristoranteId", "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {
            setRedirect(true);
            setResponsePage(getApplication().getHomePage());
        }

        form = new Form<Ristorante>("ristorantePicturesForm");
        add(form);
        form.setOutputMarkupId(true);
        form.setMultiPart(true);
        form.setMaxSize(Bytes.megabytes(1));
        uploadField = new FileUploadField("uploadField");
        form.add(uploadField);
        form.add(new UploadProgressBar("progressBar", form));
        form.add(new SubmitButton("submitForm", form));

        picturesList = new ListView<RistorantePicture>("picturesList", ristorante.getPictures()) {
            @Override
            protected void populateItem(final ListItem<RistorantePicture> item) {
                //Button disabled, because the getPicture is not yet implemented
                item.add(new AjaxFallbackButton("publish-unpublish", form) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        item.getModelObject().setActive(!item.getModelObject().isActive());
                        try {
                            ristorantePictureService.save(item.getModelObject());
                            ristorante = ristoranteService.getByID(ristorante.getId());
                        } catch (JackWicketException e) {
                            error(getString("genericErrorMessage"));
                        }
                        if (target != null) {
                            target.addComponent(getFeedbackPanel());
                            target.addComponent(form);
                        }
                    }
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        if(item.getModelObject().isActive()){
                            tag.getAttributes().put("title", getString("button.unpublish"));
                            tag.getAttributes().put("class", "unpublishPictureButton");
                        }
                        else{
                            tag.getAttributes().put("title", getString("button.publish"));
                            tag.getAttributes().put("class", "publishPictureButton");
                        }
                    }
                }.setVisible(false));
                item.add(new AjaxFallbackButton("remove", form) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        try {
                            RistorantePicture picture = item.getModelObject();
                            ristorante.getPictures().remove(picture);
                            ristoranteService.updateNoRevision(ristorante);
                            ristorantePictureService.remove(picture);
                            ristorante = ristoranteService.getByID(ristorante.getId());
                        } catch (JackWicketException e) {
                            error(getString("genericErrorMessage"));
                        }
                        if (target != null) {
                            target.addComponent(getFeedbackPanel());
                            target.addComponent(form);
                        }
                    }
                });
                item.add(new Image("picture", new DynamicImageResource() {
                    @Override
                    protected byte[] getImageData() {
                        return item.getModelObject().getPicture();
                    }
                }));
            }
        };
        picturesList.setOutputMarkupId(true);
        picturesList.setReuseItems(true);
        form.add(picturesList);

    }

    private class SubmitButton extends AjaxFallbackButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Ristorante> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            final FileUpload upload = uploadField.getFileUpload();
            if (upload != null) {
                ristorante.addPicture(new RistorantePicture(upload.getBytes(), true));
                try {
                    ristoranteService.updateNoRevision(ristorante);
                    ristorante = ristoranteService.getByID(ristorante.getId());
                    picturesList.setModelObject(ristorante.getPictures());
                } catch (JackWicketException e) {
                    getFeedbackPanel().error(getString("An error occurred"));
                }
            }
            if (target != null) {
                target.addComponent(form);
                target.addComponent(getFeedbackPanel());
            }
        }
    }

}
