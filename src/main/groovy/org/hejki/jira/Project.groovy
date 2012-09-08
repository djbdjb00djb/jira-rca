package org.hejki.jira

import groovy.transform.ToString
/**
 * JIRA Project representation.
 * <p>Fields: (fields marked with # are not implemented)
 * <ul>
 *     <li>id
 *     <li>key
 *     <li>name
 *     <li>self
 *     <li>url
 *     <li>description
 *     <li>lead
 *     <li>components - lazy
 *     <li># issueTypes
 *     <li># assigneeType
 *     <li>versions - lazy
 *     <li># roles
 *     <li># avatarUrls
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class Project extends JiraObject {
    private int id
    private String key
    private String name
    private URL self
    private URL url
    private String description
    private User lead

    private Project(map) {
        fillFromMap(map)
    }

    public static List<Project> list() {
        return list('project', {new Project(it)})
    }

    /**
     * Find a project by the specified key.
     *
     * @return the finded project or null if none was found
     */
    public static Project findByKey(String key) {
        def json = RestClient.instance.get("project/$key")
        return new Project(json)
    }

    /**
     * Fetch all supported fields from the server.
     *
     * @return this
     */
    public Project load() {
        return fillFromMap(rest.get("project/$key"))
    }

    /**
     * Fetch and return a full representation of a the project's versions.
     *
     * @param expand
     * @return list of project's versions
     */
    public List<Version> versions(boolean expand = false) {
        return list("project/$key/versions${expand ? '?expand' : ''}", {new Version(it)})
    }

    /**
     * Fetch and return a full representation of a the specified project's components.
     *
     * @return list of project's components
     */
    public List<Component> components() {
        return list("project/$key/components", {new Component(it)})
    }
}
