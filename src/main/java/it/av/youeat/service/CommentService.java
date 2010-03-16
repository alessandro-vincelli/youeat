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

import it.av.youeat.ocm.model.Comment;
import it.av.youeat.ocm.model.Eater;

import java.util.Collection;

/**
 * Operations on Comments
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface CommentService {

    /**
     * Save a comment
     * 
     * @param comment
     * @return Comment
     */
    Comment save(Comment comment);

    /**
     * Get all the comment
     * 
     * @return Collection<Comment>
     */
    Collection<Comment> getAll();

    /**
     * Remove comment from the data base
     * 
     * @param comment
     */
    void remove(Comment comment);

    /**
     * Remove comments from the data base
     * 
     * @param comments
     */
    void remove(Collection<Comment> comments);

    /**
     * remove all the comments added by the given users
     * 
     * @param eater
     */
    void removeByEater(Eater eater);

    /**
     * get all the comments of the given eater
     * 
     * @param eater
     * @return user's comments
     */
    Collection<Comment> getByEater(Eater eater);
}