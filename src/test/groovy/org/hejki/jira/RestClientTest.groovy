package org.hejki.jira

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.junit.*

class RestClientTest extends TestBase {
    static RestClient client
    HttpServer server

    @BeforeClass
    static void initSharedInstance() {
        super.setUp()
        client = RestClient.initSharedInstance("http://localhost:${p('test.port')}")
        client.connectTimeout = 500
        client.readTimeout = 1000
    }

    @Before
    void initServer() {
        server = HttpServer.create(new InetSocketAddress(p('test.port').toInteger()), 0)
    }

    @After
    void stopServer() {
        server.stop(0)
    }

    @Test
    void convertResponseJson() {
        def stream = new ByteArrayInputStream('{"key":"value","array":["a","b"]}'.bytes)

        def json = client.convertResponse(stream.text, 'application/json')
        Assert.assertEquals('value', json.key)
        Assert.assertEquals(2, json.array.size())
        Assert.assertEquals('a', json.array[0])
        Assert.assertEquals('b', json.array[1])
    }

    @Test
    void convertPlainText() {
        def stream = new ByteArrayInputStream('hello'.bytes)
        Assert.assertEquals('hello', client.convertResponse(stream.text, 'text/plain'))
    }

    Closure<HttpHandler> handler = {int responseCode, String expectedMethod, String responseData = null, String expectedData = '' ->
        [handle: {HttpExchange exchange ->
            Assert.assertEquals(expectedMethod, exchange.requestMethod)
            Assert.assertEquals(expectedData, exchange.requestBody.text)
            def data = responseData?.bytes

            exchange.responseHeaders.add('Content-Type', 'plain/text')
            exchange.sendResponseHeaders(responseCode, data ? data.length : 0)
            if (data) {
                exchange.responseBody.write(data)
            }
            exchange.close()
        }] as HttpHandler
    }

    @Test
    void get() {
        server.createContext('/test', handler(HttpURLConnection.HTTP_OK, 'GET', 'OK'))
        server.start()

        Assert.assertEquals('OK', client.get('test'))
    }

    @Test
    void get_404() {
        server.createContext('/none', handler(HttpURLConnection.HTTP_NOT_FOUND, 'GET'))
        server.start()

        Assert.assertNull(client.get('none'))
    }

    @Test
    void delete() {
        server.createContext('/del', handler(HttpURLConnection.HTTP_NO_CONTENT, 'DELETE'))
        server.start()

        Assert.assertTrue(client.delete('del'))
    }

    @Test
    void delete_200() {
        server.createContext('/del', handler(HttpURLConnection.HTTP_OK, 'DELETE', 'OK'))
        server.start()

        Assert.assertFalse(client.delete('del'))
    }

    @Test
    void post() {
        server.createContext('/posts', handler(HttpURLConnection.HTTP_OK, 'POST', 'OK', 'data?'))
        server.start()

        Assert.assertEquals('OK', client.post('posts', 'data?'))
    }

    @Test
    void post_404() {
        server.createContext('/posts', handler(HttpURLConnection.HTTP_NOT_FOUND, 'POST', 'Error 404', 'new data'))
        server.start()

        Assert.assertNull(client.post('posts', 'new data'))
    }

    @Test
    void put() {
        server.createContext('/puts', handler(HttpURLConnection.HTTP_OK, 'PUT', 'OK', 'data'))
        server.start()

        Assert.assertEquals('OK', client.put('puts', 'data'))
    }

    @Test
    void put_404() {
        server.createContext('/puts', handler(HttpURLConnection.HTTP_NOT_FOUND, 'PUT', 'Error', 'data'))
        server.start()

        Assert.assertNull(client.put('puts', 'data'))
    }
}