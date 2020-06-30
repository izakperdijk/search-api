package com.ordina.aiops.splunk.searchapi.utility;

public interface Utils {

    //region example-input
    // Regular search
    String exGreensSalesSummary = "| summary \"ex_linearreg_greens_sales\"";

    // Applying a model to query parameters (actual query and REST-compatible-syntax)
    String exApplyModel1 = "| makeresults | eval \"Sq Ft\"=5.8, Inventory=700, \"Amt on Advertising\"=11.5,\"No of Competing Stores\"=20 | apply ex_linearreg_greens_sales as \"Predicted_Net_Sales\"";
    String exApplyModel2 = "$&makeresults&$&eval&@Sq&Ft@=5.8,&Inventory=700,&@Amt&on&Advertising@=11.5,@No&of&Competing&Stores@=20&$&apply&ex_linearreg_greens_sales&as&@Predicted_Net_Sales@";
    //endregion

    // When passed via REST, queries need to be parsed back to their regular form
    static String parse(String rest) {
        return rest.replaceAll("\\$", "|")
                .replaceAll("&", " ")
                .replaceAll("@", "\"");
    }

}
