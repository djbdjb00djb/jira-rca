package org.hejki.jira
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * Handles REST requests.
 * Contains methods for handling HTTP requests. Supports GET,
 * POST, PUT and DELETE methods. For GET and POST returns response converted
 * to corresponding type.
 * <ul>
 *     <li>text/plain - String
 *     <li>application/json - Map converted from JSON
 *
 * @author Petr Hejkal
 */
@Slf4j
class RestClient {
    /** Shared instance. */
    private static RestClient instance;

    /** URL to server. */
    private final String server
    /** Store Basic authentization for HTTP requests. */
    private String authorization

    /**
     * Creates a new instance from XML configuration.
     *
     * @param configurationUri URI to file with xml configuration
     */
    private RestClient(String configurationUri) {
        def config = new XmlSlurper().parse(configurationUri)
        this.server = config.server.text()
        setAuthorization(config.username.text(), config.password.text())

        if (config.@trustall.toBoolean()) {
            trustAll()
        }
    }

    /**
     * Creates a new instance with manual configuration.
     *
     * @param server URL to JIRA server
     * @param userName JIRA username
     * @param password JIRA user password
     */
    private RestClient(server, userName, password) {
        this.server = server
        setAuthorization(userName, password)
    }

    /**
     * Manual initialization method for users which do not want use XML configuration on classpath.
     *
     * @param server URL to JIRA server
     * @param username JIRA username
     * @param password JIRA user password
     * @return a new initialized REST client instance
     */
    public static RestClient initSharedInstance(String server, String username, String password) {
        instance = new RestClient(server, username, password)
        return instance
    }

    /**
     * Returns shared instance of REST client which is initialized by XML configuration
     * file on classpath.
     *
     * @throws IllegalStateException if config.xml file is not on classpath
     * @return REST cleint instance
     */
    public static RestClient getInstance() {
        if (!instance) {
            def configURL = RestClient.class.getResource('/config.xml')
            if (!configURL) {
                throw new IllegalStateException('Cannot find configuration file "config.xml" on classpath. You may use manual initialization by calling method RestClient.initSharedInstance.')
            }
            instance = new RestClient(configURL.toURI().toString())
        }
        return instance
    }

    /**
     * Set authorization parameters.
     *
     * @param userName JIRA username
     * @param password JIRA user password
     * @return this
     */
    public RestClient setAuthorization(String userName, String password) {
        this.authorization = "Basic ${"$userName:$password".getBytes().encodeBase64().toString()}"
        return this
    }

    /**
     * Performs GET HTTP action and returns response data in corresponding format.
     * Successful response was response with code 200.
     *
     * @param action REST action
     * @return converted response data if response is success, otherwise returns null
     */
    public Object get(String action) {
        HttpURLConnection connection = obtainConnection(action, 'GET')

        int responseCode = connection.responseCode
        if (responseCode == 200) {
            def response = convertResponse(connection.inputStream.text, connection.contentType)
            logInfo(connection, action, response)
            return response
        } else {
            logError(connection, action)
        }
    }

    /**
     * Send DELETE action. Returns information about success.
     * Successful response was response with code 204.
     *
     * @param action REST action
     * @return true if delete is success (returns 204 response code), otherwise returns false
     */
    public boolean delete(action) {
        HttpURLConnection connection = obtainConnection(action, 'DELETE')

        int responseCode = connection.responseCode
        if (responseCode == 204) {
            logInfo(connection, action)
            return true
        }

        logError(connection, action)
        return false
    }

    /**
     * Send POST action and returns response in data corresponding format.
     * Successful response was response with code 200, 201 or 204.
     *
     * @param action REST action
     * @param data data for response in JSON format (after data.toString() call)
     * @return converted response data if response is success, otherwise returns null
     */
    public Object post(String action, def data) {
        HttpURLConnection connection = obtainConnection(action, 'POST', true, true)
        writeRequestData(connection, data)

        int responseCode = connection.responseCode
        if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
            def response = convertResponse(connection.inputStream.text, connection.contentType)
            logInfo(connection, action, response, data)
            return response
        } else {
            logError(connection, action, data)
        }
    }

    /**
     * Performs PUT HTTP action and returns response data in corresponding format.
     * Successful response was response with code 200.
     *
     * @param action REST action
     * @param data data for send in request
     * @return converted response data if response is success, otherwise returns null
     */
    public Object put(String action, def data) {
        HttpURLConnection connection = obtainConnection(action, 'PUT', true, true)
        writeRequestData(connection, data)

        int responseCode = connection.responseCode
        if (responseCode == 200) {
            def response = convertResponse(connection.inputStream.text, connection.contentType)
            logInfo(connection, action, response, data)
            return response
        } else {
            logError(connection, action, data)
        }
    }

    /**
     * Write a data to the connection's output stream and flush this stream.
     *
     * @param contentType the data content type. The default is 'application/json'
     * @param encoding the data charset. The default is 'utf-8'
     */
    private void writeRequestData(HttpURLConnection connection, def data, String contentType = 'application/json', String charset = 'utf-8') {
        connection.setRequestProperty('Content-Type', "$contentType; charset=$charset")
        connection.outputStream.write(data?.toString().getBytes(charset))
        connection.outputStream.flush()
    }

    /**
     * Creates a HTTP connection to server. If username is set then Basic authorization
     * was added to request headers. Created URL was "$server/rest/api/2/$action".
     *
     * @param action REST action
     * @param method HTTP method
     * @param doInput set the flag to true if you intend to use the URL connection for input, false if not. The default is true.
     * @param doOutput set the flag to true if you intend to use the URL connection for output, false if not. The default is false.
     */
    private HttpURLConnection obtainConnection(String action, String method, doInput = true, doOutput = false) {
        HttpURLConnection connection = "$server/rest/api/2/$action".toURL().openConnection()
        connection.requestMethod = method
        connection.doInput = doInput
        connection.doOutput = doOutput

        if (authorization) {
            connection.setRequestProperty("Authorization", authorization)
        }
        return connection
    }

    /**
     * Log info message about request/response.
     *
     * @param connection connection
     * @param action action
     * @param response response data, default null
     * @param data request data, default null
     */
    private void logInfo(HttpURLConnection connection, String action, def response = null, def data = null) {
        org.hejki.jira.RestClient.log.info("${connection.requestMethod} '$action' ${connection.responseCode}")
        if (data) {org.hejki.jira.RestClient.log.info(" * data: $data")}
        org.hejki.jira.RestClient.log.info(" * response type: ${connection.contentType}")
        if (response) {org.hejki.jira.RestClient.log.info(" * response body: $response")}
    }

    /**
     * Log error message with logger.
     */
    private void logError(HttpURLConnection connection, String action, def data = null) {
        org.hejki.jira.RestClient.log.error("${connection.requestMethod} '$action' error ${connection.responseCode}")
        if (data) {org.hejki.jira.RestClient.log.error(" * data: $data")}
        org.hejki.jira.RestClient.log.error(" * response type: ${connection.contentType}")
        org.hejki.jira.RestClient.log.error(" * error message: ${connection.errorStream}")
    }

    /**
     * Converts text from response to propriet object by given content type.
     * <ul>
     *     <li><b>application/json</b> JSON object representation
     *     <li><b>text/plain</b> returns given text
     *     <li>If other content type is present then returns given text
     */
    private def convertResponse(String text, String contentType) {
        if (contentType.startsWith('application/json')) {
            return new JsonSlurper().parseText(text)
        }
        if (contentType.startsWith('text/plain')) {
            return text
        }
        return text
    }

    /**
     * Sets HTTPS communication to trust all server certificates.
     */
    static trustAll() {
        def trustAllCerts = new X509TrustManager() {
            void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            X509Certificate[] getAcceptedIssuers() {
                return null
            }
        }

        def sc = SSLContext.getInstance("SSL")
        sc.init(null, [trustAllCerts] as TrustManager[], new SecureRandom())

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier([verify: {hostname, session -> true}] as HostnameVerifier)
    }
}