package com.ordina.aiops.splunk.searchapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidentField {

    @JsonProperty("Status")
    private String status;
    @JsonProperty("TicketOwner")
    private String ticketOwner;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("IncidentID")
    private String incidentID;
    @JsonProperty("Field1")
    private String field1;
    @JsonProperty("Field2")
    private String field2;
    @JsonProperty("Field3")
    private String field3;

    public String toQueryArgs() {

        return "\"Status\"=\"" + status + "\"," +
                "\"TicketOwner\"=\"" + ticketOwner + "\"," +
                "\"Category\"=\"" + category + "\"," +
                "\"IncidentID\"=\"" + incidentID + "\"," +
                "\"Field1\"=\"" + field1 + "\"," +
                "\"Field2\"=\"" + field2 + "\"," +
                "\"Field3\"=\"" + field3 + "\"";

    }

}
