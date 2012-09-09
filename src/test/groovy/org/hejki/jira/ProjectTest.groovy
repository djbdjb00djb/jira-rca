package org.hejki.jira
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class ProjectTest extends TestBase {
    static String prKey
    static Project sandbox

    @BeforeClass
    public static void loadSandbox() {
        prKey = p('project.key')
        sandbox = Project.findByKey(prKey)
    }

    @Test
    public void findByKey() throws Exception {
        assertProject(sandbox, false)
    }

    @Test
    public void listAndLoad() throws Exception {
        def projects = Project.list()
        def sandbox = projects.find({it.key == prKey})

        Assert.assertTrue(projects.size() > 0);
        assertProject(sandbox, true)

        sandbox.load()
        assertProject(sandbox, false)
    }

    private void assertProject(Project project, boolean shortVersion) {
        Assert.assertEquals(prKey, project.key);
        Assert.assertEquals(p('project.name'), project.name);
        if (shortVersion) {
            Assert.assertNull(project.description);
            Assert.assertNull(project.lead);
        } else {
            Assert.assertEquals(p('project.descr'), project.description);
            Assert.assertEquals(p('user.name'), project.lead.name);
        }
    }

    @Test
    public void versions() throws Exception {
        def versions = sandbox.versions()

        Assert.assertEquals(2, versions.size());
        Assert.assertEquals(p('version.name'), versions[0].name);
        Assert.assertEquals(p('version.descr'), versions[0].description);
        Assert.assertTrue(versions[0].released);
        Assert.assertEquals(p('version2.name'), versions[1].name);
        Assert.assertNull(versions[1].description);
        Assert.assertFalse(versions[1].released);
    }

    @Test
    public void versionByName() throws Exception {
        def version = sandbox.versionByName(p('version.name'))

        Assert.assertNotNull(version);
        Assert.assertEquals(p('version.name'), version.name);
        Assert.assertEquals(p('version.descr'), version.description);
    }

    @Test
    public void versionByName_nonFound() throws Exception {
        Assert.assertNull(sandbox.versionByName('Non Exist Version'));
    }

    @Test
    public void components() throws Exception {
        def components = sandbox.components()

        Assert.assertTrue(components.size() > 0);
        Assert.assertEquals(p('component.name'), components[0].name);
        Assert.assertEquals(p('component.descr'), components[0].description);
    }

    @Test
    public void componentByName() throws Exception {
        def component = sandbox.componentByName(p('component.name'))

        Assert.assertNotNull(component);
        Assert.assertEquals(p('component.name'), component.name);
        Assert.assertEquals(p('component.descr'), component.description);
    }

    @Test
    public void componentByName_nonFound() throws Exception {
        Assert.assertNull(sandbox.componentByName('Non Exist Component'));
    }

}
