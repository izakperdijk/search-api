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

    static String incident() {

        return "{" +
            "\"Incident\": {" +
                "\"Category\": \"incident\"," +
                "\"IncidentID\": \"IM10181\"," +
                "\"Status\": \"Open\"," +
                "\"TicketOwner\": \"rested\"" +
            "}," +
            "\"Messages\": [" +
                "\"The assignment group is invalid.\"," +
                "\"Select valid assignment group.\"" +
            "]," +
            "\"ReturnCode\": 71" +
        "}";

    }

    static String incidentToQueryArgs(String incidentStringJSON) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        Incident incident = objectMapper.readValue(incidentStringJSON, Incident.class);

        return incident.toString();

    }

}
