package com.ordina.aiops.splunk.searchapi.controller;

import org.springframework.http.HttpStatus;
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

    // Create a saved search, f.i. "search-test-from-api" "| summary \"ex_linearreg_greens_sales\""
    @GetMapping("/saved/{id}/{query}")
    public ResponseEntity<String> createSavedSearch(@PathVariable String id, @PathVariable String query) throws IOException {

        splunkService.savedSearch(id, parse(query));
        return new ResponseEntity<>("Splunk saved search " + id + " was created successfully.", HttpStatus.OK);

    }

    // Query saved search, f.i. "search-test-from-api"
    @GetMapping("/saved/{id}")
    public ResponseEntity<String> querySavedSearch(@PathVariable String id) throws IOException {

        splunkService.savedSearch(id);
        return new ResponseEntity<>("Splunk saved search called successfully.", HttpStatus.OK);

    }

    // Regular search, f.i. "| summary \"ex_linearreg_greens_sales\""
    @GetMapping("/{query}")
    public ResponseEntity<String> search(@PathVariable String query) throws IOException {

        splunkService.search(parse(query));
        return new ResponseEntity<>("Splunk search completed successfully.", HttpStatus.OK);

    }

}
