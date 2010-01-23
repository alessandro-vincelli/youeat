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
package it.av.youeat.web.panel;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.components.ImagesAvatar;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Display the avatar for the user
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class EaterAvatarPanel extends Panel {
 
    /**
     * @param id component id
     * @param model model for eater
     */
    public EaterAvatarPanel(String id, IModel<Eater> model, Page parentPage) {
        super(id, model);
        add(ImagesAvatar.getAvatar("avatar", model.getObject(), parentPage, true));
    }
}