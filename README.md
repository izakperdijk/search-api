# search-api
This is a prototype project to interface Splunk search using Spring Boot and the Splunk Enterprise SDK.

### Setup and Requirements
* An active [`Splunk`](https://www.splunk.com/en_us/download/splunk-enterprise.html) local instance
* [`splunk-sdk-java-1.6.5.jar`](https://dev.splunk.com/enterprise/docs/java/sdk-java/gettingstartedsdkjava/requirementssdkjava/)
* `$HOME$/Documents/Search-API-config/connection.properties`
including the following fields:
    * `spl.un = <username>` (local Splunk admin username)
    * `spl.pwd = <password>` (local Splunk admin password)
    * `spl.host = localhost`
    * `spl.port = 8089`
* Build the project
* Build the sdk
* Run the application

### How to use
- Run any search using
`/search/{id}` (doesn't work, runs placeholder model application instead)
- Query a saved search using
`/search/saved/{id}` (works)
- Create a saved search using
`/search/saved/{id}/{query}` (doesn't work)
