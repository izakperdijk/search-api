package com.ordina.aiops.splunk.searchapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/search")
public class MainController {

    // Connect to the Splunk Service
    private static final SplunkService splunkService = new SplunkService();

    // Summary query (regular search, unused)
    private static final String greensSalesSummary = "| summary \"ex_linearreg_greens_sales\"";

    // Applying a model to query parameters (placeholder for regular search)
    private static final String applyModel = "| makeresults | eval \"Sq Ft\"=5.8, Inventory=700, \"Amt on Advertising\"=11.5,\"No of Competing Stores\"=20 | apply ex_linearreg_greens_sales as \"Predicted_Net_Sales\"";

    /* TODO Create a saved search // "search-test-from-api" "| summary \"ex_linearreg_greens_sales\""
       TODO Find out how to let the user enter queries normally and translate them to rest properly
     */
    @GetMapping("/saved/{id}/{query}")
    public ResponseEntity<String> createSavedSearch(@PathVariable String id, @PathVariable String query) throws IOException {

        splunkService.savedSearch(id, query);
        return new ResponseEntity<>("Splunk saved search " + id + " was created successfully.", HttpStatus.OK);

    }

    // Query saved search // "search-test-from-api"
    @GetMapping("/saved/{id}")
    public ResponseEntity<String> querySavedSearch(@PathVariable String id) throws IOException {

        splunkService.savedSearch(id);
        return new ResponseEntity<>("Splunk saved search called successfully.", HttpStatus.OK);

    }

    // TODO Regular search (for now has placeholder applyModel which applies a model to query parameters to make a prediction)
    @GetMapping("/{id}")
    public ResponseEntity<String> search(@PathVariable String id) throws IOException {

        splunkService.search(applyModel);
        return new ResponseEntity<>("Splunk search completed successfully.", HttpStatus.OK);

    }

}
