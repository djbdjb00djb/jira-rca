package org.hejki.jira

import org.junit.Assert
import org.junit.Test

class IssueTypeTest {
    @Test
    public void list() throws Exception {
        def list = IssueType.list()
        def bug = list.find {it.name == 'Bug'}

        Assert.assertTrue(list.size() > 4);
        assertBug bug
    }

    @Test
    public void findById() throws Exception {
        assertBug IssueType.findById(1)
    }

    @Test
    public void findByName() throws Exception {
        assertBug IssueType.findByName('Bug')
    }

    def assertBug = {IssueType bug ->
        Assert.assertNotNull(bug);
        Assert.assertEquals(1, bug.id);
        Assert.assertFalse(bug.subtask);
        Assert.assertNotNull(bug.self);
        Assert.assertNotNull(bug.description);
        Assert.assertNotNull(bug.iconUrl);
    }
}