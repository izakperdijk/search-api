# search-api
This is a prototype project to interface Splunk search using Spring Boot and the Splunk Enterprise SDK.

### Setup and Requirements
* An active [`Splunk`](https://www.splunk.com/en_us/download/splunk-enterprise.html) local instance
* [`splunk-sdk-java-1.6.5.jar`](https://dev.splunk.com/enterprise/docs/java/sdk-java/gettingstartedsdkjava/requirementssdkjava/)
* `$HOME/Documents/Search-API-config/connection.properties`
including the following fields:
    * `spl.un = <username>` (local Splunk admin username)
    * `spl.pwd = <password>` (local Splunk admin password)
    * `spl.host = localhost`
    * `spl.port = 8089`
    * `sm9.get.url = http://localhost:9090/SM/9/rest/incidents/`
    * `sm9.put.url = http://localhost:9090/__admin/mappings/`
* Build the project
* Build the sdk
* [`wiremock-standalone-2.26.3.jar`](http://wiremock.org/docs/running-standalone/)
* Run Wiremock with param `-port 9090`
* Run the `search-api` application

### How to use
- Run any search using
`/search/{query}`
- Query a saved search using
`/search/saved/{id}`
- Create a saved search using
`/search/saved/{id}/{query}`
- Apply an existing model to supplied args, returning a specifically labeled output
`/search/{modelid}/{output}/{args}`
- Perform some operation on an existing model
`/search/{modelid}/{query}`
- Apply the model on all mocked incidents (ft. `Wiremock` see `/mappings` folder) and update them:
`/search/incident`
- `¡CAUTION!` Clean an index (empty it entirely) using
`/search/clean/{index}`

where
* `modelid`/`id`/`output`/`index` is any valid Splunk (model / saved search / label / index) identifier
* `query` is any regular Splunk search query but URL-encoded (UTF-8)
* `args` are individual key-value pairs but URL-encoded (UTF-8)

#####Encoding Queries
- Search the web for percent or URL encoding to encode your query into the required format
- Do the encoding from some frontend application
