package com.ordina.aiops.splunk.searchapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Incident {

        @JsonProperty("Incident")
        private HashMap<String, String> incident;
        @JsonProperty("Messages")
        private String[] messages;
        @JsonProperty("ReturnCode")
        private String returnCode;

        public String toString() {

                ArrayList<String> arrl = new ArrayList<>();
                incident.forEach((key, value) -> arrl.add("" + key + "=" + value));
                String inc = String.join(", ", arrl);
                String msg = "Messages=" + "\"" + Arrays.toString(messages) + "\"";
                return inc + ", " + msg + ", ReturnCode=" + returnCode;

        }

}
