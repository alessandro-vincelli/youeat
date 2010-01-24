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
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistorantePicture;
import it.av.youeat.service.RistorantePictureService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.components.ButtonOpenRisto;
import it.av.youeat.web.components.ImageRisto;

import java.util.List;

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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

/**
 * Add and Remove picture on {@link Ristorante}.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class RistoranteEditPicturePage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "ristoranteService")
    private RistoranteService ristoranteService;
    @SpringBean
    private RistorantePictureService ristorantePictureService;

    private Form<Ristorante> pictForm;
    private FileUploadField uploadField;
    private ListView<RistorantePicture> picturesList;
    private final String ristoranteId;

    /**
     * @param ristorante
     * @throws YoueatException
     */
    public RistoranteEditPicturePage(Ristorante ristorante) throws YoueatException {
        this(new PageParameters(YoueatHttpParams.RISTORANTE_ID + "=" + ristorante.getId()));
    }

    /**
     * @param parameters
     * @throws YoueatException
     */
    public RistoranteEditPicturePage(PageParameters parameters) throws YoueatException {
        add(getFeedbackPanel());
        ristoranteId = parameters.getString(YoueatHttpParams.RISTORANTE_ID, "");
        if (StringUtils.isNotBlank(ristoranteId)) {
            //this.ristorante = ristoranteService.getByID(ristoranteId);
        } else {
            setRedirect(true);
            setResponsePage(getApplication().getHomePage());
        }

        pictForm = new Form<Ristorante>("ristorantePicturesForm", new LoadableDetachableModel<Ristorante>() {
            @Override
            protected Ristorante load() {
                return ristoranteService.getByID(ristoranteId);
            }
        });
        add(pictForm);
        pictForm.setOutputMarkupId(true);
        pictForm.setMultiPart(true);
        pictForm.setMaxSize(Bytes.megabytes(1));
        uploadField = new FileUploadField("uploadField");
        pictForm.add(uploadField);
        pictForm.add(new UploadProgressBar("progressBar", pictForm, uploadField));
        pictForm.add(new SubmitButton("submitForm", pictForm));

        picturesList = new ListView<RistorantePicture>("picturesList", new PicturesModel()) {
            @Override
            protected void populateItem(final ListItem<RistorantePicture> item) {
                // Button disabled, because the getPicture is not yet implemented
                item.add(new AjaxFallbackButton("publish-unpublish", pictForm) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        item.getModelObject().setActive(!item.getModelObject().isActive());
                        try {
                            ristorantePictureService.save(item.getModelObject());
                        } catch (YoueatException e) {
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
                        if (item.getModelObject().isActive()) {
                            tag.getAttributes().put("title", getString("button.unpublish"));
                            tag.getAttributes().put("class", "unpublishPictureButton");
                        } else {
                            tag.getAttributes().put("title", getString("button.publish"));
                            tag.getAttributes().put("class", "publishPictureButton");
                        }
                    }
                }.setVisible(false));
                item.add(new AjaxFallbackButton("remove", pictForm) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        try {
                            RistorantePicture picture = item.getModelObject();
                            Ristorante risto = ((Ristorante)getForm().getModelObject());
                            risto.getPictures().remove(picture);
                            ristoranteService.updateNoRevision(risto);
                            ristorantePictureService.remove(picture);
                            picturesList.setModel(new PicturesModel());
                        } catch (YoueatException e) {
                            error(getString("genericErrorMessage"));
                        }
                        if(target != null){
                            target.addComponent(pictForm);
                            target.addComponent(getFeedbackPanel());  
                        }
                        
                    }
                });
                item.add(ImageRisto.getImage("picture", item.getModelObject().getPicture(), 130, 130, false));
            }
        };
        picturesList.setOutputMarkupId(true);
        picturesList.setReuseItems(false);
        pictForm.add(picturesList);

        ButtonOpenRisto buttonOpenAddedRisto = new ButtonOpenRisto("buttonOpenAddedRisto",  pictForm.getModel(), true);
        add(buttonOpenAddedRisto);

        ButtonOpenRisto buttonOpenAddedRistoRight = new ButtonOpenRisto("buttonOpenAddedRistoRight", pictForm.getModel(), true);
        add(buttonOpenAddedRistoRight);
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
                Ristorante risto = ((Ristorante)form.getModelObject());
                risto.addPicture(new RistorantePicture(upload.getBytes(), true));
                try {
                    ristoranteService.updateNoRevision(risto);
                    picturesList.setModel(new PicturesModel());
                } catch (YoueatException e) {
                    getFeedbackPanel().error(getString("genericErrorMessage"));
                }
            }
            if (target != null) {
                target.addComponent(form);
                target.addComponent(getFeedbackPanel());
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            super.onError(target, form);
            target.addComponent(getFeedbackPanel());
        }
    }

    private class PicturesModel extends LoadableDetachableModel<List<RistorantePicture>> {
        public PicturesModel() {
            super();
        }

        public PicturesModel(List<RistorantePicture> pictures) {
            super(pictures);
        }

        @Override
        protected List<RistorantePicture> load() {
            return ristoranteService.getByID(ristoranteId).getPictures();
        }
    }

}
