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
    private List<Version> versions
    private List<Component> components

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
        return find("project/$key", {new Project(it)})
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
     * @param cachedResultsIfPossible false if you want fetch versions from server, otherwise
     * first check if any versions in project field are set if no then returns fetched list from
     * server. Default value is true.
     * @return list of project's versions
     */
    public List<Version> versions(boolean cachedResultsIfPossible = true) {
        if (!cachedResultsIfPossible || !versions) {
            versions = list("project/$key/versions", {new Version(it)})
        }
        return versions
    }

    /**
     * Returns a project version with the specified name.
     *
     * @param name a version name
     * @param cachedResultsIfPossible false if you want fetch versions from server. Default is true
     * @return the found version or null if no version was found
     * @see #versions(boolean)
     */
    public Version versionByName(String name, boolean cachedResultsIfPossible = true) {
        return versions(cachedResultsIfPossible)?.find() {it.name == name}
    }

    /**
     * Fetch and return a representation of a the specified project's components.
     * Fetched components will have filled only basic fields, lead or assignee field
     * will not be filled.
     *
     * @param cachedResultsIfPossible false if you want fetch components from server, otherwise
     * first check if any components in project field are set if no then returns fetched list from
     * server. Default value is true.
     * @return list of project's components
     * @see Component#load()
     */
    public List<Component> components(boolean cachedResultsIfPossible = true) {
        if (!cachedResultsIfPossible || !components) {
            components = list("project/$key/components", {new Component(it)})
        }
        return components
    }

    /**
     * Returns a project component with the specified name.
     *
     * @param name a component name
     * @param cachedResultsIfPossible false if you want fetch versions from server. Default is true
     * @return the found component or null if no component was found
     * @see #components(boolean)
     */
    public Component componentByName(String name, boolean cachedResultsIfPossible = true) {
        return components(cachedResultsIfPossible)?.find() {it.name == name}
    }
}
