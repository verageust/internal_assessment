package com.company;

// Class responsible for storing financial information
public class FinancialData {
    private double income;
    private double expenses;
    private double savings;

    public FinancialData() {
        this.income = 0.0;
        this.expenses = 0.0;
        this.savings = 0.0;
    }

    // Getter and setter methods for income
    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    // Getter and setter methods for expenses
    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    // Getter and setter methods for savings
    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }
}
