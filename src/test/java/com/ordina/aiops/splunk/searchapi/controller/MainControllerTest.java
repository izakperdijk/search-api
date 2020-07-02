package com.ordina.aiops.splunk.searchapi.controller;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static com.ordina.aiops.splunk.searchapi.utility.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MainControllerTest {

    @Test
    public void testParse() throws UnsupportedEncodingException {

        assertEquals(decode("%7C+summary+%22ex_linearreg_greens_sales%22"),
                "| summary \"ex_linearreg_greens_sales\"");
        assertEquals(decode("%7C+makeresults+%7C+eval+%22Sq+Ft%22%3D5.8%2C+Inventory%3D700%2C+%22Amt+on+Advertising%22%3D11.5%2C%22No+of+Competing+Stores%22%3D20+%7C+apply+ex_linearreg_greens_sales+as+%22Predicted_Net_Sales%22"),
                "| makeresults | eval \"Sq Ft\"=5.8, Inventory=700, \"Amt on Advertising\"=11.5,\"No of Competing Stores\"=20 | apply ex_linearreg_greens_sales as \"Predicted_Net_Sales\"");

    }

}
