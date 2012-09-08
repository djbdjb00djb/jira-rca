package org.hejki.jira

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RestClientTest {
    private RestClient client

    @Before
    public void setUp() throws Exception {
        client = new RestClient('localhost', 'CIA', 'TopSecret')
    }

    @Test
    public void convertResponseJson() throws Exception {
        def stream = new ByteArrayInputStream('{"key":"value","array":["a","b"]}'.bytes)

        def json = client.convertResponse(stream.text, 'application/json')
        Assert.assertEquals('value', json.key);
        Assert.assertEquals(2, json.array.size());
        Assert.assertEquals('a', json.array[0]);
        Assert.assertEquals('b', json.array[1]);
    }

    @Test
    public void convertPlainText() throws Exception {
        def stream = new ByteArrayInputStream('hello'.bytes)
        Assert.assertEquals('hello', client.convertResponse(stream.text, 'text/plain'));
    }
}