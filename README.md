JIRA-RCA
========

Introduction
------------

JIRA-RCA is JIRA REST Client API for Groovy. Provides API for common tasks with
projects and issues in Atlassian's JIRA project tracker. API is based
on [JIRA 5.1.4 REST API](http://docs.atlassian.com/jira/REST/latest/).

Features
--------

**User**
+ find by username

**Project**
+ list of projects
+ find by key
+ load details
+ find version by name
+ load list of versions
+ find component by name
+ load list of components

**Project version**
+ create a new version
+ delete a version
+ move versions
+ modify a version properties

**Issue**
+ create a new issue
+ find by id/key
+ load list of watchers
+ add user to watcher list
+ remove user from watcher list

**Issue type**
+ list of issue types
+ find by id/name

**Issue priority**
+ list of issue priorities
+ find by id/name

Usage
-----

**Init REST client manualy.** You can alternatively init client by XML configuration
file on classpath (run script create-config.sh for creating configuration for tests).

```groovy
RestClient.initSharedInstance(restUrl, username, password)
RestClient.trustAll()
```

**Create a new version.**

```groovy
Version newVersion = new Version(name: '1.0', description: 'Basic version', releaseDate: new Date() + 14)

newVersion.create('PROJKEY')
newVersion.move('Last')
```

**Search and release old version.**

```groovy
Project project = Project.findByKey('PROJKEY')
Version oldVersion = project.versionByName('0.1')

oldVersion.modify(released: true)
```

**Create a new issue.**

```groovy
Project project   = Project.findByKey('PROJKEY')
IssueType task    = IssueType.findByName('Task')
Priority priority = Priority.findByName('Minor')
Component core    = project.componentByName('core')

Issue issue = new Issue(
    project: project,
    summary: 'Problem with print in IE',
    issueType: task,
    assignee: project.lead,
    reporter: project.lead,
    priority: priority,
    labels: ['spring', 'problem'],
    originalEstimate: '4h',
    remainingEstimate: '4d',
    versions: [oldVersion],
    environment: 'IE 6',
    description: 'It sucks.',
    dueDate: new Date() - 1,
    fixVersions: [newVersion],
    components: [core]
)
issue.create()
```

For more examples look at tests.

Building
--------

### Requirements

* [Maven](http://maven.apache.org) 3+
* [Java](http://java.oracle.com) 5+
* [Groovy](http://groovy.codehaus.org) 1.8+

Check out and build:

    git clone git://github.com/Hejki/jira-rca.git
    cd jira-rca
    mvn install

Copyright and license
---------------------

Copyright 2012 Petr Hejkal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
