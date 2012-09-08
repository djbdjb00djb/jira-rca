package org.hejki.jira
import groovy.json.JsonBuilder
/**
 * Base class for all JIRA objects.
 *
 * @author Petr Hejkal
 */
protected abstract class JiraObject {
    /**
     * Call rest get method with the specified action.
     * <p>Example: <code>list('project', {new Project(it)})</code>
     *
     * @param action GET action
     * @param constructObject closure for JIRA object construction
     * @return list of objects, it can be empty but not null
     */
    protected static list(String action, Closure constructObject) {
        def list = []
        def json = RestClient.instance.get(action)

        for (def singleJson : json) {
            list.add(constructObject.call(singleJson))
        }
        return list
    }

    /**
     * Parse date from string in format yyyy-mm-dd.
     */
    protected def toDate = {String string ->
        if (string) {
            Date.parse('yyyy-MM-dd', string)
        }
    }

    /**
     * Format date to string with format yyyy-mm-dd.
     */
    protected def fromDate = {Date date ->
        date?.format('yyyy-MM-dd')
    }

    /**
     * Returns shared REST Client.
     */
    protected RestClient getRest() {
        return RestClient.instance;
    }

    /**
     * Convert values from this object to JSON representation.
     *
     * @param properties properties used for conversion
     * @return JSON representation of this object instance
     */
    protected JsonBuilder jsonWithFields(String... properties) {
        JsonBuilder json = new JsonBuilder()
        def content = [:]
        def clazz = this.metaClass

        for (String property : properties) {
            def metaProperty = clazz.getMetaProperty(property)
            def propertyValue = metaProperty.getProperty(this)

            switch (metaProperty.type) {
                case Date.class:
                    propertyValue = fromDate(propertyValue)
                    break
            }
            content.put(property, propertyValue)
        }

        json(content)
        return json
    }

    /**
     * Fill properties in this object from the map.
     *
     * @param map the map created from json object fetched from server
     * @return this
     */
    protected def fillFromMap(def map) {
        def clazz = this.metaClass

        for (def property : map) {
            def metaProperty = clazz.getMetaProperty(property.key)
            if (metaProperty) {
                def value = property.value

                switch (metaProperty.type) {
                    case int.class:
                    case Integer.class:
                        value = value.toInteger()
                        break
                    case Date.class:
                        value = toDate(value)
                        break
                    case URL.class:
                        value = new URL(value)
                        break
                    case User.class:
                        value = new User(value)
                        break
                }

                metaProperty.setProperty(this, value)
            }
        }
        return this
    }
}
