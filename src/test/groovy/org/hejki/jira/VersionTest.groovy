package org.hejki.jira
import org.junit.*

class VersionTest extends TestBase {
    Version version
    static Project sandbox

    @BeforeClass
    public static void loadSandbox() {
        sandbox = Project.findByKey(p('project.key'))
    }

    @Before
    public void create() throws Exception {
        version = sandbox.versionByName('test', false)
        if (version) {
            Assert.assertTrue(version.delete())
        }

        version = new Version(
                name: 'test',
                description: 'Test version description',
                archived: true,
                released: true,
                releaseDate: '2012-09-01'
        )
        version.create(sandbox)

        Assert.assertNotNull(version.id);
        Assert.assertNotNull(version.self);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_withoutProject() throws Exception {
        new Version().create(null)
    }

    @Test
    public void move_after() throws Exception {
        def versions = sandbox.versions(false)

        assertPosition(versions, [1,2,'test'])
        version.move(versions[0].id) // move after 1.0
        assertPosition(sandbox.versions(false), [1,'test',2])
    }

    @Test
    public void move_position() throws Exception {
        def versions = sandbox.versions(false)

        assertPosition(versions, [1,2,'test'])
        version.move('First')
        assertPosition(sandbox.versions(false), ['test',1,2])
        version.move('Last')
        assertPosition(sandbox.versions(false), [1,2,'test'])
        version.move('Earlier')
        assertPosition(sandbox.versions(false), [1,'test',2])
        version.move('Later')
        assertPosition(sandbox.versions(false), [1,2,'test'])
    }

    @Test
    public void modify() throws Exception {
        version.name = 'change'
        version.description = 'dsr change'
        Version v = version.modify()

        Assert.assertSame(version, v);
        Assert.assertEquals('change', v.name);
        Assert.assertEquals('dsr change', v.description);
    }

    @Test
    public void modify_specifyParameters() throws Exception {
        version.name = 'change'
        Version v = version.modify(description: 'dscr change')

        Assert.assertSame(version, v);
        Assert.assertEquals('test', v.name); // change replace from response
        Assert.assertEquals('dscr change', v.description);
    }

    private void assertPosition(def versions, def positions) {
        for (int i = 0; i < positions.size(); i++) {
            def pos = positions[i]
            if (pos == 'test') {
                Assert.assertEquals('test', versions[i].name);
            } else if (pos == 1) {
                Assert.assertEquals(p('version.name'), versions[i].name);
            } else {
                Assert.assertEquals(p('version2.name'), versions[i].name);
            }
        }
    }

    @Test(expected = IllegalStateException)
    public void delete_withoutId() throws Exception {
        new Version().delete()
    }

    @Test(expected = IllegalStateException)
    public void move_withoutId() throws Exception {
        new Version().move(1)
    }

    @Test(expected = IllegalStateException)
    public void modify_withoutId() throws Exception {
        new Version().modify()
    }

    @After
    public void delete() {
        Assert.assertTrue(version.delete())
    }

}
