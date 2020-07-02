package com.ordina.aiops.splunk.searchapi.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public interface Utils {

    // When passed via REST, queries need to be URL-decoded
    static String decode(String rest) throws UnsupportedEncodingException {
        
        String other = rest.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(other, StandardCharsets.UTF_8.name());

    }

}
