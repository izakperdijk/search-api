package com.ordina.aiops.splunk.searchapi.service;

import com.ordina.aiops.splunk.searchapi.config.*;
import com.splunk.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import static com.ordina.aiops.splunk.searchapi.utility.job.JobUtils.*;

@Service
public class SplunkService {

    // Service to Splunk server
    public com.splunk.Service service;

    SplunkService() {
        service = Connection.getService();
    }

    public String savedSearch(String id) throws IOException {

        SavedSearch viewSavedSearch = service.getSavedSearches().get(id);

        // Run a saved search and poll for completion
        System.out.println("Run the '" + viewSavedSearch.getName() + "' search ("
                + viewSavedSearch.getSearch() + ")\n");
        Job job = null;

        // Run the saved search
        try {
            job = viewSavedSearch.dispatch();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return getResultsJSON(processJob(Objects.requireNonNull(job)));

    }

    public String savedSearch(String id, String query) throws IOException {

        // Create the new Saved Search
        service.getSavedSearches().create(id, query);
        // And query it
        return savedSearch(id);

    }

    // Run a regular search
    public String search(String query) throws IOException {

        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(query, jobargs);

        return getResultsJSON(processJob(job));

    }

    public String applyModel(String query) throws IOException {

        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(query, jobargs);

        String output = query.substring(query.indexOf(" as ") + 4)
                .replaceAll("\"", "");

        return getModelResults(processJob(job), output);

    }

    public void export() throws IOException {

        // Specify JSON as the output mode for results
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        InputStream is = service.export("search source=tcp:80", resultsArgs);
        ResultsReaderJson resultsReader = new ResultsReaderJson(is);

        HashMap<String, String> event;
        String results = null;

        while ((event = resultsReader.getNextEvent()) != null) {
            for (String key: event.keySet()) {
                if (key.equals("_raw")) {
                    results = event.get(key);
                    System.out.println(results);
                }
            }
        }

    }

    public void clean(String index) {

        Index myIndex = service.getIndexes().get(index);
        myIndex.clean(10000000);

    }


    public void collect() throws IOException {

        search("search source=new-input sourcetype=incident_json index=incidents_new | collect index=incidents_train source=train-set addtime=false");

    }

}
