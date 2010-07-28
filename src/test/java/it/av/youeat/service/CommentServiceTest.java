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
package it.av.youeat.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.Comment;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.util.DateUtil;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CommentServiceTest extends YoueatTest {
    @Autowired
    private EaterService eaterService;
    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private CommentService commentService;

    private Eater user = new Eater();
    private Ristorante rist = new Ristorante();

    @Before
    @Transactional
    public void setUp() {
        super.setUp();

        user.setFirstname("Alessandro");
        user.setLastname("Vincelli");
        user.setPassword("secret");
        user.setEmail("a.commentService@test.com");
        user.setLanguage(getLanguage());
        user.setCountry(getNocountry());

        user = eaterService.addRegolarUser(user);

        rist.setName("RistoTest");
        rist.setCity(getNocity());
        rist.setCountry(getNocountry());
        rist = ristoranteService.insert(rist, user);
    }

    @After
    @Transactional
    public void tearDown() {
        eaterService.remove(user);
        ristoranteService.remove(rist);
    }

    @Test
    public void testCommentsGeneric() {

        Comment comment = new Comment();
        comment.setTitle("ArticleTest");
        comment.setAuthor(user);
        comment.setCreationTime(DateUtil.getTimestamp());
        commentService.save(comment);

        assertEquals(comment.getTitle(), "ArticleTest");
        assertEquals(comment.getAuthor().getFirstname(), "Alessandro");

        Collection<Comment> comments = commentService.getByEater(user);
        assertTrue(comments.size() == 1);

        commentService.remove(comment);

    }

    @Test
    public void testComments_getByEater() {

        Comment comment = new Comment();
        comment.setTitle("ArticleTest");
        comment.setAuthor(user);
        comment.setCreationTime(DateUtil.getTimestamp());
        commentService.save(comment);

        Collection<Comment> comments = commentService.getByEater(user);
        assertTrue(comments.size() == 1);

    }

    @Test
    public void testComments_removeByEater() {

        Comment comment = new Comment();
        comment.setTitle("ArticleTest");
        comment.setAuthor(user);
        comment.setCreationTime(DateUtil.getTimestamp());
        commentService.save(comment);

        commentService.removeByEater(user);
        Collection<Comment> comments = commentService.getByEater(user);
        assertTrue(comments.size() == 0);

    }

    @Test
    public void testComments_removeCollection() {

        Comment comment = new Comment();
        comment.setTitle("ArticleTest");
        comment.setAuthor(user);
        comment.setCreationTime(DateUtil.getTimestamp());
        commentService.save(comment);

        commentService.remove(commentService.getAll());
        Collection<Comment> comments = commentService.getAll();
        assertTrue(comments.size() == 0);

    }
}