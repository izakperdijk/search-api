package com.ordina.aiops.splunk.searchapi.controller;

import com.splunk.TcpInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.ordina.aiops.splunk.searchapi.utility.Utils.*;

import java.io.IOException;

@RestController
@RequestMapping("/search")
public class MainController {

    // Connect to the Splunk Service
    private static final SplunkService splunkService = new SplunkService();

    // Create a saved search, f.i. "search-test-from-api", "| summary \"ex_linearreg_greens_sales\""
    @GetMapping("/saved/{id}/{query}")
    public ResponseEntity<String> createSavedSearch(@PathVariable String id, @PathVariable String query) throws IOException {

        return ResponseEntity.ok()
                .header("Splunk saved search " + id + " was created successfully.")
                .body(splunkService.savedSearch(id, decode(query)));

    }

    // Query saved search, f.i. "search-test-from-api"
    @GetMapping("/saved/{id}")
    public ResponseEntity<String> querySavedSearch(@PathVariable String id) throws IOException {

        return ResponseEntity.ok()
                .header("Splunk saved search " + id + " called successfully.")
                .body(splunkService.savedSearch(id));

    }

    @GetMapping("/model/{id}/{output}/{args}")
    public ResponseEntity<String> apply1(@PathVariable String id,
                                         @PathVariable String output,
                                         @PathVariable String args) throws IOException {

        String results = splunkService.applyModel("| makeresults | eval " + decode(args) + " | apply " + id + " as " + output);
/*
        TcpInput myInput = (TcpInput) SplunkService.service.getInputs().get("80");

        myInput.submit("{\"Field1\":\"c\", \"Field2\":\"c\", \"Field3\":\"1\"}");
        myInput.submit("{\"Field1\":\"c\", \"Field2\":\"d\", \"Field3\":\"2\"}");
        myInput.submit("{\"Field1\":\"d\", \"Field2\":\"a\", \"Field3\":\"2\"}");
        myInput.submit("{\"Field1\":\"d\", \"Field2\":\"d\", \"Field3\":\"1\"}");
        myInput.submit("{\"Field1\":\"b\", \"Field2\":\"d\", \"Field3\":\"2\"}");
        myInput.submit("{\"Field1\":\"a\", \"Field2\":\"a\", \"Field3\":\"1\"}");
        myInput.submit("{\"Field1\":\"d\", \"Field2\":\"c\", \"Field3\":\"2\"}");
        myInput.submit("{\"Field1\":\"c\", \"Field2\":\"a\", \"Field3\":\"2\"}");
        myInput.submit("{\"Field1\":\"b\", \"Field2\":\"b\", \"Field3\":\"1\"}");
        myInput.submit("{\"Field1\":\"d\", \"Field2\":\"b\", \"Field3\":\"2\"}");
 */
        return ResponseEntity.ok()
                .header("Applied the model " + id + " using query arguments " + decode(args) + ". Output saved as " + output)
                .body(results);

    }

    @GetMapping("/model/{query}")
    public ResponseEntity<String> apply2(@PathVariable String query) throws IOException {

        return ResponseEntity.ok()
                .header("Applied a model using query " + query)
                .body(splunkService.applyModel(decode(query)));

    }

    @GetMapping("/incident")
    public ResponseEntity<String> conv() throws IOException {

        String convertedIncident = incidentToQueryArgs(incident());
        System.out.println(convertedIncident);
        return ResponseEntity.ok()
                .header("Incident converted correctly")
                .body(splunkService.applyModel(
                        "| makeresults | eval " +
                        decode("Field1%3D%22a%22%2C+Field2%3D%22c%22") +
                        ", " + convertedIncident +
                        " | apply " + "test_cat" + " as " + "Predicted_Field3")
                );

    }

    // Regular search, f.i. "| summary \"ex_linearreg_greens_sales\""
    @GetMapping("/{query}")
    public ResponseEntity<String> search(@PathVariable String query) throws IOException {

        return ResponseEntity.ok()
                .header("Splunk search completed successfully.")
                .body(splunkService.search(decode(query)));

    }

}
