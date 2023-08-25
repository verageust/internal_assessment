package com.company;
import java.util.Scanner;

public class Income {
    private double monthlySalary;

    public Income(String name, double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public double getAnnualIncome() {
        return monthlySalary * 12;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your salary: ");
        double monthlySalary = scanner.nextDouble();

        System.out.println("Monthly Salary: " + monthlySalary());
        System.out.println("Annual Income: " + annualIncome());

        scanner.close();
    }
}



