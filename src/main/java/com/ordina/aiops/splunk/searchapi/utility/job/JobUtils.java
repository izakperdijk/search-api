package com.ordina.aiops.splunk.searchapi.utility.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splunk.Job;
import com.splunk.JobResultsArgs;
import com.splunk.ResultsReaderJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

public interface JobUtils {

    static Job processJob(Job job) {

        System.out.println("Waiting for the job to finish...");

        // Wait for the job to finish
        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("QUERY:\n" + job.getSearch());

        // Get results in JSON format
        return job;

    }

    static String getResultsJSON(Job job) throws IOException {

        // Specify JSON as the output mode for results
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        // Display results in JSON using ResultsReaderJson
        InputStream jobResults = job.getResults(resultsArgs);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jobResults));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();

        // Convert the String(Builder) contents into a JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jn = objectMapper.readTree(sb.toString());

        // We're only interested in the "results" field
        String results = jn.get("results").toString();

        // Returns results bundled in JSON format in a String[] (as a String)
        System.out.println("RESULTS:\n" + results);

        return Objects.requireNonNull(results);

    }

    static String getModelResults(Job job, String output) throws IOException {

        // Specify JSON as the output mode for results
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        // Display results in JSON using ResultsReaderJson
        InputStream jobResults = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader = new ResultsReaderJson(jobResults);
        HashMap<String, String> event;
        String results = null;

        while ((event = resultsReader.getNextEvent()) != null) {
            for (String key: event.keySet()) {
                if (key.equals(output)) {
                    results = event.get(key);
                }
            }
        }

        // Return results as a String
        System.out.println("RESULTS:\n" + output + " = " + results);

        return Objects.requireNonNull(results);

    }
}
