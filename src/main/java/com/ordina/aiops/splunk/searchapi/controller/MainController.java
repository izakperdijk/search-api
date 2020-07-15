package com.ordina.aiops.splunk.searchapi.controller;

import com.ordina.aiops.splunk.searchapi.service.SplunkService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SplunkService splunkService;

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

    // Apply an existing model to supplied args, returning a specifically labeled output
    @GetMapping("/model/{id}/{output}/{args}")
    public ResponseEntity<String> apply1(@PathVariable String id,
                                         @PathVariable String output,
                                         @PathVariable String args) throws IOException {

        return ResponseEntity.ok()
                .header("Applied the model " + id + " using query arguments " + decode(args) + ". Output saved as " + output)
                .body(splunkService.applyModel("| makeresults | eval " + decode(args) + " | apply " + id + " as " + output));

    }

    // Perform some operation on an existing model
    @GetMapping("/model/{query}")
    public ResponseEntity<String> apply2(@PathVariable String query) throws IOException {

        return ResponseEntity.ok()
                .header("Applied a model using query " + query)
                .body(splunkService.applyModel(decode(query)));

    }

    // Regular search, f.i. "| summary \"ex_linearreg_greens_sales\""
    @GetMapping("/{query}")
    public ResponseEntity<String> search(@PathVariable String query) throws IOException {

        return ResponseEntity.ok()
                .header("Splunk search completed successfully.")
                .body(splunkService.search(decode(query)));

    }

    @GetMapping("/clean/{index}")
    public ResponseEntity<String> clean(@PathVariable String index) {

        splunkService.clean(index);
        return ResponseEntity.ok()
                .header("Cleaned index" + index)
                .body("");

    }

    /*
    @GetMapping("/export")
    public ResponseEntity<String> export() throws IOException {

        splunkService.export();
        return ResponseEntity.ok()
                .header("Exported events.")
                .body("");

    }
    */

    /*
    @GetMapping("/collect")
    public ResponseEntity<String> collect() throws IOException {

        splunkService.collect();
        return ResponseEntity.ok()
                .header("Moved all incidents from " + "incidents_new" + " to " + "incidents_train")
                .body("");

    }
    */

}
