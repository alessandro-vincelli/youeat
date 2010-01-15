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
package it.av.eatt.service;

import static org.junit.Assert.assertEquals;
import it.av.eatt.ocm.model.Comment;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterProfile;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.ocm.util.DateUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CommentServiceTest {
    @Autowired
    @Qualifier("userService")
    private EaterService userService;
    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CountryService countryService;
    @Autowired EaterProfileService eaterProfileService;
    
    @Test
    public void testUsersBasic() {
        EaterProfile eaterProfile = new EaterProfile();
        eaterProfile.setName("ProfileTest");
        eaterProfileService.save(eaterProfile);

        Country nocountry = new Country("xx", "xxx", "test country");
        countryService.save(nocountry);

        Eater a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("a.commentService@test.com");
        a.setCountry(nocountry);
        a.setUserProfile(eaterProfile);
        a = userService.addRegolarUser(a);

        Ristorante rist = new Ristorante();
        rist.setName("RistoTest");
        rist = ristoranteService.insert(rist, a);

        Comment comment = new Comment();
        comment.setTitle("ArticleTest");
        comment.setAuthor(a);
        comment.setCreationTime(DateUtil.getTimestamp());
        commentService.save(comment);

        assertEquals(comment.getTitle(), "ArticleTest");
        assertEquals(comment.getAuthor().getFirstname(), "Alessandro");

        commentService.remove(comment);

        ristoranteService.remove(rist);
        userService.remove(a);

    }
}