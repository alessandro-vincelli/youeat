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
package it.av.youeat.web.itest;

import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.service.impl.RistoranteServiceHibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thoughtworks.selenium.DefaultSelenium;
import com.ttdev.wicketpagetest.MockableSpringBeanInjector;
import com.ttdev.wicketpagetest.WebPageTestContext;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class WebPageIntegrationTest {

    @BeforeClass
    public static void setUp() throws Exception {
        WebPageTestContext.beforePageTests();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        WebPageTestContext.afterPageTests();
    }

    @Test
    public void testWebPageGeneric() {

        MockableSpringBeanInjector.mockBean("ristoranteService", new RistoranteServiceHibernate() {

            @Override
            public List<Ristorante> getRandom(int numberOfResult) {
                List<Ristorante> ristos = new ArrayList<Ristorante>(1);
                Ristorante risto = new Ristorante();
                risto.setName("RistoAlessandro");
                risto.setCity(new City());
                ristos.add(risto);
                return ristos;
            }

            @Override
            public int count() {
                return 1;
            }

        });
        DefaultSelenium selenium = WebPageTestContext.getSelenium();
        selenium.open("/");
        System.out.println(selenium.getBodyText());
        assertTrue(StringUtils.contains(selenium.getBodyText(), "RistoAlessandro"));
        selenium.click("link=Info");
        selenium.waitForPageToLoad("3000");
        assertTrue(StringUtils.contains(selenium.getBodyText(), "YouEat vuole essere: "));

    }
}