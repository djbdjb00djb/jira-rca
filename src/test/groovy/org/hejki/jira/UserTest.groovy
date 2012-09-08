package org.hejki.jira

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class UserTest extends TestBase {
    static User hejki

    @Test
    public void list() throws Exception {
        println User.list()
    }

    @BeforeClass
    public static void findUser() {
        hejki = User.findByName(p('user.name'))
    }

    @Test
    public void findByName() throws Exception {
        Assert.assertEquals(p('user.name'), hejki.name);
        Assert.assertEquals(p('user.email'), hejki.emailAddress);
        Assert.assertEquals(p('user.dispname'), hejki.displayName);
        Assert.assertTrue(hejki.active);
    }
}
