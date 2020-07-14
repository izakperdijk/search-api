package com.ordina.aiops.splunk.searchapi.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordina.aiops.splunk.searchapi.model.Incident;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public interface Utils {

    // When passed via REST, queries need to be URL-decoded
    static String decode(String rest) throws UnsupportedEncodingException {
        
        String other = rest.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(other, StandardCharsets.UTF_8.name());

    }


    // To-be-updated field is hardcoded for now
    static Incident updateIncident(Incident incident, String predictedVal) {

        incident.getIncident().setField3(predictedVal);
        return incident;

    }

    // Temp wrapper to allow PUTting the updated incident into WireMock
    static String wrapResponse(String updatedIncidentString, String incidentID) {

        return String.format("{ " +
                "\"request\":{\"method\":\"PUT\",\"url\":\"/SM/9/rest/incidents/%s\"}," +
                "\"response\": {\"status\":200,\"body\": \"%s\"" + "}," +
                "\"persistent\":true" +
                "}", incidentID, updatedIncidentString.replaceAll("\"","\\\\\""));

    }

}
