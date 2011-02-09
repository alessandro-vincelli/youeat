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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.SocialType;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * Operation in user/s
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface EaterService {

    /**
     * Update a user
     * 
     * @param eater
     * @return just updated user
     */
    Eater update(Eater eater);

    /**
     * Add a new user, if the role is empty, it's used the USER role
     * 
     * @param eater
     * @return just added user
     */
    Eater add(Eater eater);

    /**
     * Insert a new user, during the insert is also encrypted the users's password
     * 
     * @param eater
     * @return just inserted user
     */
    Eater addRegolarUser(Eater eater);

    /**
     * <b>Don't use it</b> Insert a new admin user, during the insert is also encrypted the users's password
     * 
     * @param object
     * @return just inserted user
     */
    Eater addAdminUser(Eater object);

    /**
     * Add a new facebook user
     * 
     * @param eater
     * @return just added user
     */
    Eater addFacebookUser(Eater eater);
    
    /**
     * Add a new google user
     * 
     * @param eater
     * @return just added user
     */
    Eater addGoogleUser(Eater eater);

    /**
     * Return all the users
     * 
     * @return all the users
     */
    Collection<Eater> getAll();

    /**
     * Return all the admin user
     * 
     * @return all the admin
     */
    Collection<Eater> getAllAdminUsers();

    /**
     * Search users
     * 
     * @param pattern
     * @return found users
     */
    Collection<Eater> find(String pattern);
    
    /**
     * Search user
     * 
     * @param pattern
     * @param first first result
     * @param maxResults max number of result, 0 to disable 
     * @param sortField property name on which sort, NULL to disable
     * @param isAscending is ascending sort
     * @return found users
     */
    List<Eater> find(String pattern, int first, int maxResults, String sortField, boolean isAscending);

    /**
     * Return users without relations with the given user and the given pattern the search is performed on the firstname and
     * lastname of the user
     * 
     * @param forUser
     * @param pattern
     * @return all found users
     */
    Collection<Eater> findUserWithoutRelation(Eater forUser, String pattern);

    /**
     * Return users without relations with the given user
     * 
     * @param forUser
     * @return all users not related to this user
     */
    Collection<Eater> findUserWithoutRelation(Eater forUser);

    /**
     * Remove the given user
     * 
     * @param user
     */
    @Transactional
    void remove(Eater user);

    /**
     * Return the user with this email, there is an unique constraint on the user email
     * 
     * @param email
     * @return user with the passed email
     */
    Eater getByEmail(String email);

    /**
     * Return the user by id
     * 
     * @param id
     * @return user with the passed email
     */
    Eater getByID(String id);

    /**
     * Return the user by a social UID
     * 
     * @param socialUID
     * @param socialType
     * @return user with the passed email
     */
    Eater getBySocialUID(String socialUId, SocialType socialType);

    /**
     * create/update lucene index on eaters
     */
    void indexData();

    /**
     * Takes a previously encoded password and compares it with a rawpassword after mixing in the salt and encoding that value
     * 
     * @param encPass previously encoded password
     * @param rawPass plain text password
     * @param salt salt to mix into password
     * @return true or false
     */
    boolean isPasswordValid(String encPass, String rawPass, Object salt);

    /**
     * Encodes the rawPass using a MessageDigest. If a salt is specified it will be merged with the password before encoding.
     * 
     * @param rawPass The plain text password
     * @param salt The salt to sprinkle
     * @return Hex string of password digest (or base64 encoded string if encodeHashAsBase64 is enabled.
     */
    String encodePassword(String rawPass, Object salt);

    /**
     * Send the new password by email to the given user
     * 
     * @param eater
     * @param newPassword
     */
    void sendPasswordByEmail(Eater eater, String newPassword);

    /**
     * Set a random password for the given eater, and save the eater in the database
     * 
     * @param eater
     * @return eater with updated password
     */
    Eater setRandomPassword(Eater eater);

    /**
     * count users in the DB
     * 
     * @return number of users
     */
    int count();
    
    /**
     * count users in the DB that match the given pattern.
     * Useful togheter with the find() 
     * 
     * @param pattern pattern to filter on
     * @return number of users
     */
    int count(String pattern);
}