package org.hejki.jira;

import org.junit.BeforeClass;

public abstract class TestBase {
    private static def config

    @BeforeClass
    public static void setUp() {
        config = new XmlSlurper().parse(TestBase.class.getResourceAsStream('/test-config.xml'))
    }

    protected static String p(String path) {
        return config."$path"
    }
}
