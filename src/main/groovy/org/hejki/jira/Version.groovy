package org.hejki.jira

import groovy.json.JsonBuilder
import groovy.transform.ToString
/**
 * JIRA project version representation.
 * <p>Fields: (fields marked with # are not implemented)
 * <ul>
 *     <li>id
 *     <li>description
 *     <li>name
 *     <li>self
 *     <li>archived
 *     <li>released
 *     <li>releaseDate - format yyyy-mm-dd
 *     <li># overdue
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class Version extends JiraObject {
    private int id
    private String name
    private String description
    private URL self
    private boolean archived
    private boolean released
    private Date releaseDate
    private String project

    public Version() {}

    private Version(map) {
        fillFromMap(map)
    }

    /**
     * Create a version in specified project.
     *
     * @param projectKey required parameter which must contains a key of existing project
     * @throws IllegalArgumentException if parameter projectKey not set
     * @return this
     */
    public Version create(String projectKey) {
        project = projectKey
        if (!project) {
            throw new IllegalArgumentException('Create action required project key set.')
        }

        def json = jsonWithFields('name', 'description', 'project', 'archived', 'released', 'releaseDate')
        json = rest.post('version', json)
        return fillFromMap(json)
    }

    /**
     * Delete a project version.
     *
     * @throws IllegalStateException if id not set
     * @return true if delete was successfull, otherwise returns false
     */
    public boolean delete() {
        requireId()
        return rest.delete("version/$id")
    }

    /**
     * Modify a version's sequence within a project.
     * The move version bean has 2 alternative field value pairs:
     * <ul>
     *     <li>position - An absolute position, which may have a value of 'First', 'Last', 'Earlier' or 'Later'</li>
     *     <li>after - A version to place this version after. The value should be the self link of another version</li>
     * </ul>
     *
     * Example:
     * <pre>
     *     version.move('First')   // Move version to the first position
     *     version.move('Last')    // Move version to the last position
     *     version.move('Earlier') // Move version to the earlier position
     *     version.move('Later')   // Move version to the later position
     *     version.move(1234)      // Move version after version with id 1234
     * </pre>
     *
     * @param parameter position parameter, see Example
     * @throws IllegalStateException if id not set
     * @return this
     */
    public Version move(position) {
        requireId()

        String key = 'after'
        if ('First' == position || 'Last' == position || 'Earlier' == position || 'Later' == position) {
            key = 'position'
        }

        rest.post("version/$id/move", "{\"$key\":\"$position\"}")
        return this
    }

    /**
     * Modify a version. Any fields present in the parameter will override existing values.
     * As a convenience, if a field is not present, it is silently ignored.
     *
     * <p>Example:
     * <pre>
     *     version.modify() // store all version's fields
     *     version.modify(name: 'NewName', releaseDate: '2012-12-01') // update only name and releaseDate
     * </pre>
     *
     * @param parameters map of version's fields. If it not present then all fields will be updated
     * @throws IllegalStateException if id not set
     * @return this
     */
    public Version modify(parameters = null) {
        requireId()

        def json
        if (!parameters) {
            json = jsonWithFields('name', 'description', 'archived', 'released', 'releaseDate')
        } else {
            json = new JsonBuilder(parameters)
        }

        json = rest.put("version/$id", json)
        return fillFromMap(json)
    }

    /**
     * Throws an IllegalStateException if id not set.
     */
    private void requireId() {
        if (!id) {
            throw new IllegalStateException("Action required version's id.")
        }
    }
}

