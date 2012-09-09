package org.hejki.jira

import groovy.transform.ToString

/**
 * JIRA issue priority.
 * <p>Fields:
 * <ul>
 *     <li>name
 *     <li>description
 *     <li>statusColor
 *     <li>iconUrl
 *     <li>self
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class Priority extends JiraObject {
    private int id
    private String name
    private String description
    private String statusColor
    private URL iconUrl
    private URL self

    private Priority(map) {
        fillFromMap(map)
    }

    /**
     * Returns a list of all issue priorities.
     * Returned if the priorities exists and the user has permission to view it.
     */
    public static List<Priority> list() {
        return list('priority', {new Priority(it)})
    }

    /**
     * Find an issue priority by it's id.
     *
     * @param id the issue priority id
     * @return the found issue priority or null if none was found
     */
    public static Priority findById(int id) {
        return find("priority/$id", {new Priority(it)})
    }

    /**
     * Find an issue priority by it's name.
     *
     * @param name the issue priority name
     * @return the found issue priority or null if none was found
     */
    public static Priority findByName(String name) {
        return list().find {it.name == name}
    }
}
