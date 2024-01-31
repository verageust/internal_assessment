package com.company;

import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Map to store FinancialData instances for each month:
        Map<String, FinancialData> monthData = new HashMap<>();

        // Initializing the monthData map with FinancialData objects for each month
        monthData.put("January", new FinancialData());
        monthData.put("February", new FinancialData());
        monthData.put("March", new FinancialData());
        monthData.put("April", new FinancialData());
        monthData.put("May", new FinancialData());
        monthData.put("June", new FinancialData());
        monthData.put("July", new FinancialData());
        monthData.put("August", new FinancialData());
        monthData.put("September", new FinancialData());
        monthData.put("October", new FinancialData());
        monthData.put("November", new FinancialData());
        monthData.put("December", new FinancialData());

        // using object from MonthSelection class to open the month selection window
        SwingUtilities.invokeLater(() -> new MonthSelectionFrame(monthData));
    }
}