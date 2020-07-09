package com.ordina.aiops.splunk.searchapi.controller;

import com.ordina.aiops.splunk.searchapi.config.*;
import com.splunk.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Objects;

import static com.ordina.aiops.splunk.searchapi.utility.job.JobUtils.*;

@Service
public class SplunkService {

    // Service to Splunk server
    public static final com.splunk.Service service = Connection.service;

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

}
