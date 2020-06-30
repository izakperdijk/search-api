package com.ordina.aiops.splunk.searchapi.controller;

import org.junit.jupiter.api.Test;
import static com.ordina.aiops.splunk.searchapi.utility.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MainControllerTest {

    @Test
    public void testParse() {
        assertEquals(parse("$keyword"), "|keyword");
        assertEquals(parse("$$keyword$"), "||keyword|");
        assertEquals(parse("$&keyword&$&eval&$&@key@=2"), "| keyword | eval | \"key\"=2");
        assertEquals(parse("$&makeresults&$&eval&@Sq&Ft@=5.8,&Inventory=700,&@Amt&on&Advertising@=11.5,@No&of&Competing&Stores@=20&$&apply&ex_linearreg_greens_sales&as&@Predicted_Net_Sales@"),
                "| makeresults | eval \"Sq Ft\"=5.8, Inventory=700, \"Amt on Advertising\"=11.5,\"No of Competing Stores\"=20 | apply ex_linearreg_greens_sales as \"Predicted_Net_Sales\"");
    }

}
