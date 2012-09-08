package org.hejki.jira

import groovy.json.JsonBuilder
import groovy.transform.ToString
/**
 * JIRA issue representation.
 * <p>Fields: (fields marked with # are not implemented)
 * <ul>
 *     <li>id
 *     <li>key
 *     <li>self
 *     <li>project
 *     <li>summary
 *     <li>issueType
 *     <li>assignee
 *     <li>reporter
 *     <li>priority
 *     <li>labels
 *     <li>originalEstimate
 *     <li>remainingEstimate
 *     <li># security
 *     <li>versions
 *     <li>environment
 *     <li>description
 *     <li>duedate
 *     <li>fixVersions
 *     <li>components
 *
 * @author Petr Hejkal
 */
@ToString(includeFields = true, includeNames = true)
public class Issue extends JiraObject {
    private int id
    private String key
    private URL self
    private Project project
    private String summary
    private IssueType issueType
    private User assignee
    private User reporter
    private Priority priority
    private List<String> labels
    private String originalEstimate
    private String remainingEstimate
    private List<Version> versions
    private String environment
    private String description
    private Date dueDate
    private List<Version> fixVersions
    private List<Component> components

    /**
     * Creates a new Issue instance from map.
     */
    private Issue(def map) {
        def fields = map?.fields

        fillFromMap(map)
        if (fields) {
            project = new Project(fields?.project)
            summary = fields?.summary
            issueType = new IssueType(fields?.issuetype)
            assignee = new User(fields?.assignee)
            reporter = new User(fields?.reporter)
            priority = new Priority(fields?.priority)
            labels = fields?.labels
            originalEstimate = fields?.timetracking?.originalEstimate
            remainingEstimate = fields?.timetracking?.remainingEstimate
            versions = fields?.versions?.collect {
                new Version(it)
            }
            environment = fields?.environment
            description = fields?.description
            dueDate = toDate(fields?.duedate)
            fixVersions = fields?.fixVersions?.collect {
                new Version(it)
            }
            components = fields?.components?.collect {
                new Component(it)
            }
        }
    }

    /**
     * Create a new issue.
     *
     * @return this
     */
    public Issue create() {
        String dueDateStr = fromDate(dueDate)
        def json = new JsonBuilder()

        json {
            'fields' {
                'project' {'id' project.id}
                'summary' summary
                'issuetype' {'id' issueType.id}
                'assignee' {'name' assignee.name}
                'reporter' {'name' reporter.name}
                'priority' {'id' "${priority.id}"}
                'labels' labels.each {it}
                'timetracking' {
                    'originalEstimate' originalEstimate
                    'remainingEstimate' remainingEstimate
                }
                'versions' (
                        versions.collect {
                            [id: "${it.id}"]
                        }
                )
                'environment' environment
                'description' description
                'duedate' dueDateStr
                'fixVersions' (
                        fixVersions.collect {
                            [id: "${it.id}"]
                        }
                )
                'components' (
                        components.collect {
                            [id: "${it.id}"]
                        }
                )
            }
        }

        json = rest.post('issue', json)
        return fillFromMap(json)
    }

    public static Issue findById(int id) {
        return findByKey(id.toString())
    }

    public static Issue findByKey(String key) {
        def map = RestClient.instance.get("issue/$key?fields=*all")
        return new Issue(map)
    }

    /**
     * Returns the list of watchers for this issue.
     */
    public List<User> watchers() {
        def watchers = []
        def map = rest.get("issue/$id/watchers")

        if (map?.watchers) {
            for (def it : map.watchers) {
                watchers.add(new User(it))
            }
        }
        return watchers
    }

    /**
     * Adds a user to this issue's watcher list.
     *
     * @param watcher a new watcher
     * @throws IllegalArgumentException if username not set
     * @return true if user was added successfully, otherwise returns false
     */
    public boolean addWatcher(User watcher) {
        return addWatcher(watcher?.name)
    }

    /**
     * Adds a user to this issue's watcher list.
     *
     * @param watcher a new watcher's username
     * @throws IllegalArgumentException if username not set
     * @return true if user was added successfully, otherwise returns false
     */
    public boolean addWatcher(String watcherName) {
        if (!watcherName) {
            throw new IllegalArgumentException("Watcher must have name.")
        }

        return null != rest.post("issue/$id/watchers", "\"$watcherName\"")
    }

    /**
     * Removes a user from this issue's watcher list.
     *
     * @param watcher watcher for remove from watch list
     * @throws IllegalArgumentException if username not set
     * @return true if user was removed successfully, otherwise returns false
     */
    public boolean removeWatcher(User watcher) {
        return removeWatcher(watcher?.name)
    }

    /**
     * Removes a user from this issue's watcher list.
     *
     * @param watcher username of watcher
     * @throws IllegalArgumentException if username not set
     * @return true if user was removed successfully, otherwise returns false
     */
    public boolean removeWatcher(String watcherName) {
        if (!watcherName) {
            throw new IllegalArgumentException("Watcher must have name.")
        }

        return rest.delete("issue/$id/watchers?username=$watcherName")
    }
}

/* Not processed fields
expand: renderedFields, names, schema, transitions, operations, editmeta, changelog,
fields:[
        progress:[
                total:7200,
                progress:0,
                percent:0
        ],
        timetracking:[
                remainingEstimateSeconds:7200,
                originalEstimateSeconds:144000
        ],
        votes:[
                hasVoted:false,
                votes:0
        ],
        resolution:null,
        resolutiondate:null,
        timespent:null,
        aggregatetimeoriginalestimate:144000,
        created:2012-09-08T19:31:53.166+0200,
        updated:2012-09-08T19:31:53.166+0200,
        customfield_10001:null,
        customfield_10002:null,
        issuelinks:[],
        watches:[
                watchCount:1,
                isWatching:true,
        ],
        worklog:[total:0, startAt:0, worklogs:[], maxResults:0],
        subtasks:[],
        customfield_10100:null,
        status:[
                id:1,
                description:"The issue is open and ready for the assignee to start work on it.",
                name:Open
        ]
        workratio:0,
        attachment:[],
        aggregatetimeestimate:7200,
        :null,
        timeestimate:7200,
        aggregateprogress:[total:7200, progress:0, percent:0],
        comment:[total:0, startAt:0, comments:[], maxResults:0],
        timeoriginalestimate:144000,
        aggregatetimespent:null]
]
*/