package org.hejki.jira

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class IssueTest extends TestBase {
    static Project sandbox

    @BeforeClass
    public static void loadSandbox() {
        sandbox = Project.findByKey(p('project.key'))
    }

    @Test
    @Ignore('Remove of a newly created issues not implemented')
    public void create() throws Exception {
        def task = IssueType.findByName('Task')
        def priority = Priority.findByName('Minor')
        def versions = sandbox.versions()
        def components = sandbox.components()

        def issue = new Issue(
                project: sandbox,
                summary: 'Problem with print in IE',
                issueType: task,
                assignee: sandbox.lead,
                reporter: sandbox.lead,
                priority: priority,
                labels: ['spring', 'problem'],
                originalEstimate: '10h',
                remainingEstimate: '3d',
                versions: versions,
                environment: 'IE 6',
                description: 'It sucks.',
                dueDate: new Date() - 1,
                fixVersions: versions,
                components: [components[0]]
        )

        issue.create()

        Assert.assertNotNull(issue.id);
        Assert.assertNotNull(issue.key);
        Assert.assertNotNull(issue.self);
    }

    @Test
    public void findByKey() throws Exception {
        def issue = Issue.findByKey(p('issue.key'))

        Assert.assertEquals(p('issue.key'), issue.key);
        Assert.assertEquals(p('issue.summary'), issue.summary);
        Assert.assertEquals(p('project.key'), issue.project.key);
        Assert.assertEquals(p('project.name'), issue.project.name);
        Assert.assertEquals(p('issue.type'), issue.issueType.name);
        Assert.assertEquals(p('user.name'), issue.assignee.name);
        Assert.assertEquals(p('user.name'), issue.reporter.name);
        Assert.assertEquals(p('issue.priority'), issue.priority.name);
        Assert.assertEquals(p('issue.label1'), issue.labels[0]);
        Assert.assertEquals(p('issue.label2'), issue.labels[1]);
        Assert.assertEquals(p('issue.version'), issue.versions[0].name);
        Assert.assertEquals(p('issue.fixversion'), issue.fixVersions[0].name);
        Assert.assertEquals(p('issue.descr'), issue.description);
        Assert.assertEquals(p('component.name'), issue.components[0].name);
        Assert.assertEquals(p('component.descr'), issue.components[0].description);
        Assert.assertEquals(p('issue.orest'), issue.originalEstimate);
        Assert.assertEquals(p('issue.reest'), issue.remainingEstimate);
        println issue
    }

    @Test
    public void watchers() throws Exception {
        def issue = Issue.findByKey(p('issue.key'))
        def watchers = issue.watchers()

        println watchers
        Assert.assertEquals(2, watchers.size());
    }

    @Test
    @Ignore('Badly repeatable')
    public void addWatcher() throws Exception {
        def issue = Issue.findByKey(p('issue.key'))
        def watchers = issue.watchers()

        Assert.assertEquals(1, watchers.size());
        Assert.assertTrue(issue.addWatcher(p('issue.watcher')));

        watchers = issue.watchers()
        Assert.assertEquals(2, watchers.size());
    }

    @Test
    @Ignore('Badly repeatable')
    public void removeWatcher() throws Exception {
        def issue = Issue.findByKey(p('issue.key'))
        def watchers = issue.watchers()

        Assert.assertEquals(2, watchers.size());
        Assert.assertTrue(issue.removeWatcher(p('issue.watcher')));

        watchers = issue.watchers()
        Assert.assertEquals(1, watchers.size());
    }
}
