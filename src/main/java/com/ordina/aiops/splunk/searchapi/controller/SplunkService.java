package com.ordina.aiops.splunk.searchapi.controller;

import com.ordina.aiops.splunk.searchapi.config.*;
import com.splunk.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class SplunkService {

    // Service to Splunk server
    private static final com.splunk.Service service = Connection.service;

    public void savedSearch(String id) throws IOException {

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

        processJob(Objects.requireNonNull(job));

    }

    public void savedSearch(String id, String query) throws IOException {

        // Create the new Saved Search
        service.getSavedSearches().create(id, query);
        // And query it
        savedSearch(id);

    }

    // Run a regular search
    public void search(String query) throws IOException {

        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = service.getJobs().create(query, jobargs);

        processJob(job);

    }

    // Wait for job to finish and System.out.println its results (placeholder)
    private void processJob(Job job) throws IOException {

        System.out.println("Waiting for the job to finish...\n");

        // Wait for the job to finish
        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Display results
        InputStream results = job.getResults();

        String line = null;
        System.out.println("Results from the search job as XML:\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(results, StandardCharsets.UTF_8));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

    }
}
