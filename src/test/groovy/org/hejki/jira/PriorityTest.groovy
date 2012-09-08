package org.hejki.jira

import org.junit.Assert
import org.junit.Test

class PriorityTest {
    @Test
    public void list() throws Exception {
        def list = Priority.list()

        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals('Blocker', list[0].name);
        Assert.assertNotNull(list[0].description);
        Assert.assertNotNull(list[0].statusColor);
        Assert.assertNotNull(list[0].iconUrl);
        Assert.assertNotNull(list[0].self);
        Assert.assertNotNull(list[0].id);
    }

    @Test
    public void findById() throws Exception {
        def priority = Priority.findById(1)

        Assert.assertNotNull(priority);
        Assert.assertEquals('Blocker', priority.name);
    }

    @Test
    public void findByName() throws Exception {
        def priority = Priority.findByName('Critical')

        Assert.assertNotNull(priority);
        Assert.assertEquals('Critical', priority.name);
    }
}
