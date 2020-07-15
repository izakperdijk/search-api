package com.ordina.aiops.splunk.searchapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordina.aiops.splunk.searchapi.model.Incident;
import com.ordina.aiops.splunk.searchapi.service.SplunkService;
import com.splunk.Index;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.ordina.aiops.splunk.searchapi.utility.Utils.wrapResponse;

/*
* Retrieve incident(s) from SM9; for each incident:
* (1) Incident is added to Splunk index (incidents_new)
* (2) Incident is converted into query arguments to apply a predefined ML model (irrelevant args are ignored by Splunk)
* (3) Incident predefined field is updated
* (4) Updated incident is added to Splunk index (incidents_mod)
* (5) Incident is updated in SM9
*/
@RestController
public class Pipeline {

    @Autowired
    private SplunkService splunkService;
    public static final RestTemplate restTemplate = new RestTemplate();
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String sm9get = System.getProperty("sm9.get.url");
    public static final String sm9put = System.getProperty("sm9.put.url"); // WireMock Exclusive
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Pipeline.class);

    @GetMapping("/pipeline")
    public void pipeline() throws IOException {

        LOG.info("|* Initiating pipeline *|");

        // Get the incident(s)
        List<Incident> incidents = getIncidents();

        // Handle the incidents
        for (Incident incident : incidents) {
            try {
                handleIncident(incident);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOG.info("|* Pipeline successfully executed! *|");

    }

    public void handleIncident(Incident incident) throws IOException {

        // Retrieve incident ID for future reference
        String incidentID = incident.getIncident().getIncidentID();

        // Submit the incident to Splunk index "incidents_new"
        //addToSplunkIndex("incidents_new", incident);

        // Update the incident by applying the model
        Incident updatedIncident = updateIncident(incident);

        // Submit the modified incident to Splunk index "incidents_mod"
        //addToSplunkIndex("incidents_mod", updatedIncident);

        // Update incident in SM9 (WireMock)
        updateIncidentInSM9(incidentID, updatedIncident);

    }

    public List<Incident> getIncidents() throws JsonProcessingException {

        // GET sm/9/rest/incidents
        LOG.info("Retrieving incidents from " + sm9get);

        ResponseEntity<String> incidentJSON = restTemplate.getForEntity(sm9get, String.class);
        List<Incident> newIncidents = Arrays.asList(objectMapper.readValue(incidentJSON.getBody(), Incident[].class));

        LOG.info("| List of incidents was retrieved successfully!");

        return newIncidents;

    }

    // To-be-updated field should comply to the predicted field in the ML model
    public Incident updateIncident(Incident incident) throws IOException {

        LOG.info("Applying ML model to incident " + incident.getIncident().getIncidentID());

        // Convert incident KV-pairs into query arguments
        String queryArgs = incident.toQueryArgs();

        String predictedVal = splunkService.applyModel("| makeresults | eval " + queryArgs +
                " | apply " + "test_cat" + " as " + "Predicted_Field3");

        // Update the incident field with predicted value
        incident.getIncident().setField3(predictedVal);

        LOG.info("Successfully updated Incident with ID: " + incident.getIncident().getIncidentID());
        LOG.info(" | Field3 has been updated to " + incident.getIncident().getField3());

        return incident;

    }

    public void addToSplunkIndex(String index, Incident incident) throws JsonProcessingException {

        LOG.info("Adding incident to index " + index + "\n");

        Index input = splunkService.service.getIndexes().get(index);
        input.submit(objectMapper.writeValueAsString(incident));

        LOG.info("| Addition successful!");

    }

    public void updateIncidentInSM9(String incidentID, Incident incident) throws JsonProcessingException {

        LOG.info("Updating incident " + incidentID + " in SM9 ");

        // We probably don't need to wrap the response (when not using WireMock), so we can skip this step entirely
        String updatedIncidentJSONString = objectMapper.writeValueAsString(incident);
        String wrappedIncident = wrapResponse(updatedIncidentJSONString, incidentID); // for WireMock

        // Can probably PUT the Incident.class instead (when not dealing with WireMock)
        restTemplate.put(sm9put + incidentID, wrappedIncident);

        LOG.info("| Update successful!");

    }
}
