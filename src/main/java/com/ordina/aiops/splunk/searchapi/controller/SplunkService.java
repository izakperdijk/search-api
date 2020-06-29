package com.ordina.aiops.splunk.searchapi.controller;

import com.splunk.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SplunkService {

    public void helloSplunk() throws IOException {

        // Set security protocol
        com.splunk.Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

        // Credentials
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setUsername("");
        loginArgs.setPassword("");
        loginArgs.setHost("localhost");
        loginArgs.setPort(8089);

        // Connect and login using credentials
        com.splunk.Service service = com.splunk.Service.connect(loginArgs);

        /*
        // Test to see if we're in
        for (Application app : service.getApplications().values()) {
            System.out.println(app.getName());
        }
        */

        String query = "| summary \"parkinsons_updrs\"";
        String searchName = "search-test-from-api";
        // SavedSearch savedSearch = service.getSavedSearches().create(searchName, query); // already created

        /*
        SavedSearchCollection savedSearches = service.getSavedSearches();
        System.out.println(savedSearches.size() + " saved searches are available to the current user:\n");
        for (SavedSearch entity : savedSearches.values()) {
            System.out.println("     " + entity.getName());
        }
        */
        SavedSearch viewSavedSearch = service.getSavedSearches().get(searchName);
        /*
        System.out.println("Properties for '" + viewSavedSearch.getName() + "':\n\n" +
                "Description:         " + viewSavedSearch.getDescription() + "\n" +
                "Scheduled:           " + viewSavedSearch.isScheduled() + "\n" +
                "Next scheduled time: " + viewSavedSearch.getNextScheduledTime() + "\n" +
                ""
        );
        */

        // Run a saved search and poll for completion
        System.out.println("Run the '" + viewSavedSearch.getName() + "' search ("
                + viewSavedSearch.getSearch() + ")\n");
        Job job = null;

        // Run the saved search
        try {
            job = viewSavedSearch.dispatch();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("Waiting for the job to finish...\n");

        // Wait for the job to finish
        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Display results
        InputStream results = job.getResults();
        String line = null;
        System.out.println("Results from the search job as XML:\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

    }
}
