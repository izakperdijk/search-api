package com.ordina.aiops.splunk.searchapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MainController {

    @GetMapping(value = "/")
    public ResponseEntity<String> mainMethod() throws IOException {
        new SplunkService().helloSplunk();
        return new ResponseEntity<String>("Splunk call completed successfully.", HttpStatus.OK);
    }

}
