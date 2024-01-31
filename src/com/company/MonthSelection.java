package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

// Class responsible for displaying a frame where the user can select a month
class MonthSelectionFrame {
    private static MonthSelectionFrame instance; // "instance" to ensure that there is only one instance
    // of the MonthSelectionFrame class throughout the product.
    private final JFrame frame; // The main frame
    private final Map<String, ExpenseApp> monthApps; // Map to store ExpenseApp instances for each month

    // Constructor
    public MonthSelectionFrame(Map<String, FinancialData> financialDataMap) {
        frame = new JFrame("Spending Tracker");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // The frame opens up as full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Monthly Spending Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Customizing the font
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centering the title
        contentPanel.add(titleLabel);

        monthApps = new HashMap<>(); // Initializing the map to store ExpenseApp instances

        // Creating a separate panel for the month combo box so that the box appears under the title
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        JComboBox<String> monthComboBox = new JComboBox<>(months); // Creating a combo box
        panel.add(monthComboBox);

        JButton selectButton = new JButton("Select");
        panel.add(selectButton);

        // Creating space between the title and the combo box
        contentPanel.add(Box.createVerticalStrut(20));

        contentPanel.add(panel);

        selectButton.addActionListener((ActionEvent e) -> {
            frame.dispose(); // Close the month selection frame

            // Convert the selected month from the combo box to a string
            String selectedMonth = monthComboBox.getSelectedItem().toString();
            // Open the ExpenseApp frame for the chosen month
            SwingUtilities.invokeLater(() -> new ExpenseApp(selectedMonth, financialDataMap));
        });

        frame.add(contentPanel);
        frame.setVisible(true);
        instance = this;

    }

    // Method providing access to the single instance of the MonthSelectionFrame
    public static MonthSelectionFrame getInstance() {
        return instance;

    }

    // Method to show the frame
    public void showFrame() {
        frame.setVisible(true);
    }
}