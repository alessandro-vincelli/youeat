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

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thoughtworks.selenium.DefaultSelenium;
import com.ttdev.wicketpagetest.WebPageTestBasicContext;

@ContextConfiguration(locations = "classpath:test-application-context*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class WebPageIntegrationTest {

    @BeforeClass
    public static void setUp() throws Exception {
        WebPageTestBasicContext.beforePageTests();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        WebPageTestBasicContext.afterPageTests();
    }

    @Test
    public void testWebPageGeneric() {
// TODO store something in the DB, mock not applicable anymore
// Now it's necessary add the 
//        MockableSpringBeanInjector.installInjector(this); 
//   in the Wikcet Application context
//   not useful anymore mock the bean
//        MockableSpringBeanInjector.mockBean("ristoranteService", new RistoranteServiceHibernate() {
//
//            @Override
//            public List<Ristorante> getRandom(int numberOfResult) {
//                List<Ristorante> ristos = new ArrayList<Ristorante>(1);
//                Ristorante risto = new Ristorante();
//                risto.setName("RistoAlessandro");
//                risto.setCity(new City());
//                ristos.add(risto);
//                return ristos;
//            }
//
//            @Override
//            public int count() {
//                return 1;
//            }
//
//        });
        DefaultSelenium selenium = WebPageTestBasicContext.getSelenium();
        selenium.open("/");
        //assertTrue(StringUtils.contains(selenium.getBodyText(), "RistoAlessandro"));
        selenium.click("link=Info");
        selenium.waitForPageToLoad("3000");
        assertTrue(StringUtils.contains(selenium.getBodyText(), "Free and Open Source"));

    }
}