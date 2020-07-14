package com.ordina.aiops.splunk.searchapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class Incident {

        @JsonProperty("Incident")
        private IncidentField incident;
        @JsonProperty("Messages")
        private String[] messages;
        @JsonProperty("ReturnCode")
        private String returnCode;

        public String toQueryArgs() {

                return incident.toQueryArgs() + ", " +
                        "Messages=" + "\"" + Arrays.toString(messages) + "\", " +
                        "ReturnCode=" + returnCode;

        }

}
