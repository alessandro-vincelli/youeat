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

import it.av.youeat.ocm.model.Tag;

import java.util.List;

import org.hibernate.criterion.Criterion;

/**
 * Services on Tag
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface TagService {

    /**
     * insert a new tag, the operation takes care of maintain the list without duplicated values
     * 
     * @param tag
     * @return just created Tag or already present tag
     */
    Tag insert(String tag);

    /**
     * Return all the tags
     * 
     * @return List<Tag>
     */
    List<Tag> getAll();

    /**
     * Find tags by the given string
     * 
     * @param pattern
     * @return List<Tag>
     */
    List<Tag> find(String pattern);

    /**
     * Free text search by Lucene
     * 
     * @param pattern
     * @return List<Tag>
     */
    List<Tag> freeTextSearch(String pattern);

    /**
     * Find tags by the given criterion
     * 
     * @param criterions
     * @return List<Tag>
     */
    List<Tag> findByCriteria(Criterion... criterions);

    /**
     * Return tag with the given tag value, exact match search
     * 
     * @param tagValue
     * @return Tag
     */
    Tag getByTagValue(String tagValue);

    /**
     * Remove a tag
     * 
     * @param tag
     */
    void remove(Tag tag);
}