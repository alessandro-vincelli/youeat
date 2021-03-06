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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.Dialog;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
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
public class MessageServiceTest extends YoueatTest {

    @Autowired
    private EaterService eaterService;
    @Autowired
    private DialogService dialogService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private LanguageService languageService;

    private Eater userB;
    private Eater userC;

    @Before
    public void setUp() {
        super.setUp();
        userB = new Eater();
        userB.setFirstname("Alessandro");
        userB.setLastname("Vincelli");
        userB.setPassword("secret");
        userB.setEmail("userServiceTest@test.com");
        userB.setCountry(getNocountry());
        userB.setLanguage(languageService.getAll().get(0));
        userB = eaterService.addRegolarUser(userB);
        assertNotNull("A is null", userB);

        userC = new Eater();
        userC.setFirstname("Arnaldo");
        userC.setLastname("Vincelli");
        userC.setPassword("secret");
        userC.setEmail("userServiceTest2@test.com");
        userC.setLanguage(languageService.getAll().get(0));
        userC.setCountry(getNocountry());
        userC = eaterService.addRegolarUser(userC);
        assertNotNull("C is null", userC);
    }

    @Test
    @Ignore("page needed")
    public void testMessageBasic() {
        Message msg = new Message();
        msg.setSender(userB);
        msg.setBody("body");
        msg.setTitle("title");

        Dialog dialog = dialogService.startNewDialog(userB, userC, msg, null);
        assertTrue("Created null dialog", dialog != null);
        assertTrue("Created dialog without creation time", dialog.getCreationTime() != null);
        assertTrue("Created dialog without sender", dialog.getReceiver() != null);
        assertTrue("Created dialog without receiver", dialog.getSender() != null);
        assertTrue("Created dialog without messages", dialog.getMessages() != null);
        assertTrue("Created dialog without messages", dialog.getMessages().size() == 1);

        assertTrue("Wrong number created diallogs", dialogService.countCreatedDialogs(userB) == 1);
        assertTrue("Wrong number created diallogs", dialogService.countCreatedDialogs(userC) == 0);

        assertTrue("Wrong number incoming messages", messageService.countIncomingMessages(userC) == 1);
        assertTrue("Wrong number incoming messages", messageService.countIncomingMessages(userB) == 0);
        assertTrue("Wrong number sent messages", messageService.countSentMessages(userC) == 0);
        assertTrue("Wrong number sent messages", messageService.countSentMessages(userB) == 1);

        Message msg2 = new Message();
        msg2.setSender(userC);
        msg2.setBody("body2");
        msg2.setTitle("title2");

        dialog = dialogService.reply(msg2, dialog, userB, null);
        assertTrue("Dialog contains wrong number of messages", dialog.getMessages().size() == 2);
        assertTrue(dialog.equals(dialog.getMessages().first().getDialog()));
        List<Dialog> dialogs = dialogService.getCreatedDialogs(userB);
        assertTrue("dialogs list empty", dialogs.size() == 1);

        dialogs = dialogService.getDialogs(userC, true);
        assertTrue("dialogs list empty", dialogs.size() == 1);

        dialogs = dialogService.getCreatedDialogs(userC);
        assertTrue("dialogs list empty", dialogs.size() == 0);

        long unreadMsgs = messageService.countUnreadMessages(userC);
        assertTrue(unreadMsgs == 1);

        dialogService.readDiscussion(dialog.getId(), userC);
        unreadMsgs = messageService.countUnreadMessages(userC);
        assertTrue(unreadMsgs == 0);

        unreadMsgs = messageService.countMessages(userC);
        assertTrue(unreadMsgs == 2);

    }

    @Test
    @Ignore("page needed")
    public void testRemovedMessage() {
        // Verify that after a dialog is tagged as removed it is also removed from the list
        Message msg = new Message();
        msg.setSender(userB);
        msg.setBody("body");
        msg.setTitle("title");

        Dialog dialog = dialogService.startNewDialog(userB, userC, msg, null);

        List<Dialog> dialogs = dialogService.getDialogs(userC, true);
        assertTrue("dialogs list empty", dialogs.size() == 1);

        dialogs = dialogService.getCreatedDialogs(userB);
        assertTrue("dialogs list empty", dialogs.size() == 1);

        long unreadMsgs = messageService.countUnreadMessages(userC);
        assertTrue(unreadMsgs == 1);

        // Dialog deleted by receiver
        dialogService.delete(dialog, userC);

        dialogs = dialogService.getDialogs(userC, true);
        assertTrue("dialog not removed", dialogs.size() == 0);

        dialogs = dialogService.getCreatedDialogs(userB);
        assertTrue("dialog removed from sender inbox", dialogs.size() == 1);

        unreadMsgs = messageService.countUnreadMessages(userC);
        assertTrue(unreadMsgs == 0);

        unreadMsgs = messageService.countMessages(userC);
        assertTrue(unreadMsgs == 0);

        // Dialog deleted by sender
        dialogService.delete(dialog, userB);

        dialogs = dialogService.getCreatedDialogs(userB);
        assertTrue("dialog not removed", dialogs.size() == 0);

        unreadMsgs = messageService.countMessages(userB);
        assertTrue(unreadMsgs == 0);
    }

    @Test
    @Ignore("page needed")
    public void testSuggestNewFriend() {
        // suggesting one new friend
        Set<Eater> newFriends = new HashSet<Eater>(1);
        newFriends.add(userB);
        dialogService.sendFriendSuggestions(userC, userB, newFriends, null);
        List<Dialog> dialogs = dialogService.getDialogs(userB, true);
        assertTrue(!dialogs.isEmpty());
        Dialog dialog = dialogs.get(0);
        assertTrue("Created null dialog", dialog != null);
        assertTrue(dialog.getSender().equals(userC));
        assertTrue(dialog.getMessages().first().getSender().equals(userC));
        assertTrue(StringUtils.contains(dialog.getMessages().first().getBody(), userB.getId()));
        assertTrue(StringUtils.contains(dialog.getMessages().first().getTitle(), userC.getFirstname()));

        // suggesting multiple new friends
        newFriends.add(userC);
        dialogService.sendFriendSuggestions(userB, userC, newFriends, null);
        dialogs = dialogService.getDialogs(userC, true);
        assertTrue(!dialogs.isEmpty());
        dialog = dialogs.get(0);
        assertTrue("Created null dialog", dialog != null);
        assertTrue(dialog.getSender().equals(userB));
        assertTrue(dialog.getMessages().first().getSender().equals(userB));
        assertTrue(StringUtils.contains(dialog.getMessages().first().getBody(), userB.getId()));
        assertTrue(StringUtils.contains(dialog.getMessages().first().getBody(), userC.getId()));

    }
}