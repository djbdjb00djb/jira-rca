package org.hejki.jira

import org.junit.Assert;
import org.junit.Test;

public class ComponentTest extends TestBase {
    @Test
    public void load() throws Exception {
        def component = Project.findByKey(p('project.key')).componentByName(p('component.name'))

        Assert.assertNull(component.lead);
        Assert.assertNull(component.assignee);

        component.load()
        Assert.assertNotNull(component.lead);
        Assert.assertNotNull(component.assignee);
        Assert.assertEquals(p('user.name'), component.lead.name);
        Assert.assertEquals(p('user.dispname'), component.assignee.displayName);
    }

    @Test(expected = IllegalStateException.class)
    public void load_withoutId() throws Exception {
        new Component().load()
    }
}
