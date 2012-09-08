package org.hejki.jira

import groovy.transform.ToString

/**
 * JIRA component.
 * <p>Fields: (fields marked with # are not implemented)
 * <ul>
 *     <li>id
 *     <li>name
 *     <li>self
 *     <li>description
 *     <li>lead
 *     <li># assigneeType
 *     <li>assignee
 *     <li># realAssigneeType
 *     <li># realAssignee
 *     <li># isAssigneeTypeValid
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
class Component extends JiraObject {
    private int id
    private String name
    private URL self
    private String description
    private User lead
    private User assignee

    private Component(map) {
        fillFromMap(map)
    }
}