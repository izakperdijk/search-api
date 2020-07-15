package com.ordina.aiops.splunk.searchapi.config;

import com.splunk.SSLSecurityProtocol;
import com.splunk.ServiceArgs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Connection {

    // Service to Splunk server
    public static com.splunk.Service getService() {
        return Connect();
    }

    private static com.splunk.Service Connect() {

        // Set security protocol
        com.splunk.Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

        // Configure connection properties / credentials
        try {
            setSystemProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Credentials (local admin account)
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setUsername(System.getProperty("spl.un"));
        loginArgs.setPassword(System.getProperty("spl.pwd"));
        loginArgs.setHost(System.getProperty("spl.host"));
        loginArgs.setPort(Integer.parseInt(System.getProperty("spl.port")));

        // Connect and login using credentials
        return com.splunk.Service.connect(loginArgs);
    }

    // Get externalized connection properties and credentials (local file on host)
    private static void setSystemProperties() throws IOException {

        FileInputStream propFile = new FileInputStream(
                System.getProperty("user.home")
                        + "/Documents/Search-API-config/connection.properties"
        );

        Properties p = new Properties(System.getProperties());
        p.load(propFile);
        System.setProperties(p);

    }

}
