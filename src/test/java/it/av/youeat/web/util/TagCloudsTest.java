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
package it.av.youeat.web.util;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.Tag;
import it.av.youeat.service.YoueatTest;
import it.av.youeat.service.impl.TagServiceHibernate;
import it.av.youeat.web.url.YouetGeneratorURL;
import it.av.youeat.web.util.TagCloud;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

public class TagCloudsTest extends YoueatTest {
    
    
    private TagServiceHibernate tagServiceHibernate;


    @Before
    public void setUp() {
        tagServiceHibernate = mock(TagServiceHibernate.class);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testRistoranteService() throws YoueatException, IOException {
        Map<String, Integer> map= new HashMap<String, Integer>();
        
        map.put("tag2", 2);
        map.put("tag8", 8);
        when(tagServiceHibernate.getTagsAndScore()).thenReturn(map);
        
        TagCloud tagCloud = new TagCloud();
        tagCloud.setTagService(tagServiceHibernate);
        
        String tagCloudHTML = tagCloud.getTagClound();
        String expected = "<span style=\"font-size: 9.25pt;\">tag2</span> ";
        expected =  expected +  "<span style=\"font-size: 22.0pt;\">tag8</span> ";
        assertEquals(expected, tagCloudHTML);
        
        
    }




}