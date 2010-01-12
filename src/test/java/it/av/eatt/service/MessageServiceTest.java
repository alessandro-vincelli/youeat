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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterProfile;
import it.av.eatt.ocm.model.Message;

import java.util.List;

import org.junit.After;
import org.junit.Before;
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
public class MessageServiceTest {

    @Autowired
    @Qualifier("userService")
    private EaterService userService;
    @Autowired
    private EaterProfileService userProfileService;
    @Autowired
    @Qualifier("messageService")
    private MessageService messageService;
    private EaterProfile profile;
    private Eater userB;
    private Eater userC;

    @Before
    public void setUp(){
        profile = new EaterProfile();
        profile.setName("testProfile");
        profile = userProfileService.save(profile);
        userB = new Eater();
        userB.setFirstname("Alessandro");
        userB.setLastname("Vincelli");
        userB.setPassword("secret");
        userB.setEmail("userServiceTest@test.com");
        userB.setUserProfile(profile);
        userB = userService.add(userB);
        assertNotNull("A is null", userB);

        userC = new Eater();
        userC.setFirstname("Arnaldo");
        userC.setLastname("Vincelli");
        userC.setPassword("secret");
        userC.setEmail("userServiceTest@test2.com");
        userC = userService.addRegolarUser(userC);
        assertNotNull("C is null", userC);

    }

    @After
    public void tearDown() {
        userProfileService.remove(profile);
    }

    @Test
    public void testMessageBasic() {

        Message msg = new Message();
        msg.setSender(userB);
        msg.setReceiver(userC);
        msg.setBody("body");
        msg.setTitle("title");
        msg = messageService.send(msg);
        assertNotNull(msg);
        assertNotNull(msg.getId());
        assertTrue(!msg.isReceived());

        List<Message> msgsRcv = messageService.findReceived(userC);
        assertTrue("The messages received are not 1", msgsRcv.size() == 1);
        msg = msgsRcv.get(0);
        assertTrue(msg.getBody() == "body");
        assertTrue(msg.isReceived());
        messageService.delete(msg);
        msgsRcv = messageService.findReceivedDeleted(userC);
        assertTrue("The messages received deleted are not 1", msgsRcv.size() == 1);
        messageService.purge(msg);
        msgsRcv = messageService.findReceived(userC);
        assertTrue("The messages received deleted are not 0", msgsRcv.size() == 0);

        List<Message> msgsSent = messageService.findSent(userB);
        assertTrue("The messages sent are not 1", msgsSent.size() == 1);
        msg = msgsSent.get(0);
        assertTrue(msg.getBody() == "body");
        assertTrue(!msg.isReceived());
        messageService.purge(msg);
        msgsSent = messageService.findSent(userC);
        assertTrue("The messages received deleted are not 0", msgsSent.size() == 0);

    }

}