package com.ordina.aiops.splunk.searchapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.ordina.aiops.splunk.searchapi.config.*;
import com.ordina.aiops.splunk.searchapi.model.Incident;
import com.splunk.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.ordina.aiops.splunk.searchapi.utility.Utils.*;
import static com.ordina.aiops.splunk.searchapi.utility.job.JobUtils.*;

@Service
public class SplunkService {

    // Service to Splunk server
    public static final com.splunk.Service service = Connection.service;
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String sm9get = System.getProperty("sm9.get.url");
    private static final String sm9put = System.getProperty("sm9.put.url"); // For WireMock

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

    public String pipeline() throws IOException {

        StringBuilder output = new StringBuilder();

        // Get the incident(s)
        List<Incident> incidents = getIncidents();

        // Handle the incidents
        incidents.forEach(x -> {
            try {
                /// TODO: send to Splunk before updating
                /// TODO: send to Splunk after updating
                output.append(handleIncident(x)); // Updates incident in "SM9"
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return output.toString();

    }

    public List<Incident> getIncidents() throws JsonProcessingException {

        // GET incidents at a
        ResponseEntity<String> incidentJSON = restTemplate.getForEntity(sm9get, String.class);
        return Arrays.asList(objectMapper.readValue(incidentJSON.getBody(), Incident[].class));

    }

    public String handleIncident(Incident incident) throws IOException {

        // Retrieve incident ID for future reference
        String incidentID = incident.getIncident().getIncidentID();

        // Convert incident KV-pairs into query arguments
        String queryArgs = incident.toQueryArgs();

        // Update the incident by applying the model
        Incident updatedIncident = updateIncident(incident,
                applyModel("| makeresults | eval " + queryArgs +
                        " | apply " + "test_cat" + " as " + "Predicted_Field3"));

        // If we don't need to wrap the response, we can skip this step entirely
        String updatedIncidentJSONString = objectMapper.writeValueAsString(updatedIncident);
        String wrappedIncident = wrapResponse(updatedIncidentJSONString, incidentID); // for WireMock

        // Can probably PUT the Incident.class instead
        restTemplate.put(sm9put + incidentID, wrappedIncident);

        // Feedback
        return "IncidentID: " + incidentID +
                " | Field3 has been updated to " + updatedIncident.getIncident().getField3() +"\n";

    }

}
