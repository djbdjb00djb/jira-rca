package org.hejki.jira

import groovy.transform.ToString

/**
 * Jira user.
 * <p>Fields: (fields marked with # are not implemented)
 * <ul>
 *     <li>name
 *     <li>self
 *     <li>emailAddress
 *     <li>displayName
 *     <li>active
 *     <li># timeZone
 *     <li># groups
 *     <li># expand
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class User extends JiraObject {
    private String name
    private URL self
    private String emailAddress
    private String displayName
    private boolean active

    private User(map) {
        fillFromMap(map)
    }

    /**
     * Find an user by it's username.
     *
     * @param username the user login name
     * @return the found user or null if none was found
     */
    public static User findByName(String username) {
        return find("user?username=$username", {new User(it)})
    }
}