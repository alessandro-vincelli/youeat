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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Tag;

import java.util.List;

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
public class TagServiceTest {
    @Autowired
    private TagService tagService;

    @Test
    public void testUsersBasic() throws YoueatException {
        Tag tag = tagService.insert("tag1");
        assertNotNull(tag);
        List<Tag> tags = tagService.find("tag1");
        assertNotNull(tags);
        assertTrue(tags.size() > 0);
        
        Tag tagCopy = tagService.getByTagValue(tag.getTag());
        assertNotNull(tagCopy);
        assertTrue(tagCopy.getId() == tag.getId());
        
        tags = tagService.find("tag not present");
        assertNotNull(tags);
        assertTrue(tags.size() == 0);

        Tag tag2 = tagService.insert("tag1");
        assertEquals(tag.getId(), tag2.getId());

        tagService.remove(tag);

    }

}