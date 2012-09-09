package org.hejki.jira

import groovy.transform.ToString

/**
 * JIRA issue type.
 * <p>Fields:
 * <ul>
 *     <li>id
 *     <li>name
 *     <li>self
 *     <li>description
 *     <li>iconUrl
 *     <li>subtask
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class IssueType extends JiraObject {
    private int id
    private String name
    private URL self
    private String description
    private URL iconUrl
    private boolean subtask

    private IssueType(map) {
        fillFromMap(map)
    }

    /**
     * Returns a list of all issue types visible to the user.
     */
    public static List<IssueType> list() {
        return list('issuetype', {new IssueType(it)})
    }

    /**
     * Find issue type by it's id.
     *
     * @param id the issue type id
     * @return the found issue type or null if none was found
     */
    public static IssueType findById(int id) {
        return find("issuetype/$id", {new IssueType(it)})
    }

    /**
     * Find issue type by it's name.
     *
     * @param name the issue type name
     * @return the found issue type or null if none was found
     */
    public static IssueType findByName(String name) {
        return list().find {it.name == name}
    }
}