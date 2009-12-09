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

import org.apache.wicket.ResourceReference;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;

public class SignOut extends SignOutPage {

    private static final CompressedResourceReference STYLES_CSS = new CompressedResourceReference(BasePage.class,
            "resources/styles.css");

    public SignOut() {
        super();
        add(CSSPackageResource.getHeaderContribution(STYLES_CSS));
        ResourceReference img = new ResourceReference(this.getClass(), "resources/images/logo-mela.png");
        add(new Image("logo", img));
    }

}