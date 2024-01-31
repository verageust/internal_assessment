package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
// Importing classes from the external JFree.Chart library
// that allows creation of graphs/charts:
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

// Class responsible for displaying the frame where the user adds inputs of expenses, income, etc.
public class ExpenseApp {
    // Member variables of the class:
    JPanel mainMenuPanel, incomeButtonPanel, incomeDetailPanel, expenseButtonPanel,
            expenseDetailPanel, savingUpForPanel;
    final JList<String> saveForList;
    final DefaultListModel<String> saveForListModel;
    JTextField inputSalary, inputBenefits, inputOtherIncome, totalFood, totalTransport, totalOther,
            inputRent, inputInsurance, inputEntertainment, inputClothes, inputOtherSup, incomeTextBox,
            expensesTextBox, savingsTextBox;
    private final double[] foodExpenses = new double[31];
    private final double[] transportExpenses = new double[31];
    private final double[] otherExpenses = new double[31];
    private final String dataFileName;
    double totalIncome, totalExpenses;

    // Constructor
    public ExpenseApp(String selectedMonth, Map<String, FinancialData> monthData) {
        // Constructing filenames for saving and loading data of a month
        this.dataFileName = selectedMonth.toLowerCase() + "_data.txt";

        // Creating the main frame for the spending tracker of a month
        JFrame frame = new JFrame(selectedMonth);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating a tabbed pane to allow for different tabs in the frame
        JTabbedPane tabbedPane = new JTabbedPane();

        // Creating the main menu panel
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BorderLayout());
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        tabbedPane.addTab("Main Menu", mainMenuPanel); // Creating a tab for the main menu
        tabbedPane.setBackgroundAt(0, Color.PINK);

        // Creating a left panel to allow the positioning of components to the left side of the screen
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Back button to return to month selection
        JButton backButton = new JButton("Back to Month Selection");
        backButton.setBackground(Color.PINK);
        backButton.addActionListener((ActionEvent e) -> {
            frame.dispose();
            MonthSelectionFrame.getInstance().showFrame(); // Show the month selection frame
        });

        leftPanel.add(backButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        // Creating a title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Font titleFont = new Font("Arial", Font.BOLD, 20);
        titlePanel.add(new JLabel("THIS MONTH'S OVERVIEW"));
        titlePanel.getComponent(0).setFont(titleFont);
        leftPanel.add(titlePanel);

        // Panel to show total income
        JPanel incomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        incomePanel.add(new JLabel("Total Income:"));
        incomeTextBox = new JTextField(8);
        incomeTextBox.setEditable(false); // The user cannot modify the text field
        incomePanel.add(incomeTextBox);
        incomePanel.add(new JLabel("€"));
        leftPanel.add(incomePanel);

        // Panel to show total expenses
        JPanel expensesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expensesPanel.add(new JLabel("Total Expenses:"));
        expensesTextBox = new JTextField(8);
        expensesTextBox.setEditable(false);
        expensesPanel.add(expensesTextBox);
        expensesPanel.add(new JLabel("€"));
        leftPanel.add(expensesPanel);

        // Panel to show total savings / losses
        JPanel savingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        savingsPanel.add(new JLabel("Total Savings:"));
        savingsTextBox = new JTextField(8);
        savingsTextBox.setEditable(false);
        savingsPanel.add(savingsTextBox);
        savingsPanel.add(new JLabel("€"));
        leftPanel.add(savingsPanel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(Box.createVerticalGlue());

        mainMenuPanel.add(leftPanel, BorderLayout.WEST);

        // View data button
        JButton viewDataButton = new JButton("View Data");
        viewDataButton.setBackground(Color.PINK);
        leftPanel.add(viewDataButton);

        // ActionListener to display bar chart
        viewDataButton.addActionListener((ActionEvent e) -> showBarChart(parseTextField(incomeTextBox),
                (parseTextField(expensesTextBox))));

        // The income panel and tab
        incomePanel = new JPanel();
        incomePanel.setLayout(new BorderLayout());
        TitledBorder incomeTitleBorder = BorderFactory.createTitledBorder("INCOME");
        Font incomeTitleFont = new Font("Arial", Font.BOLD, 18);
        incomeTitleBorder.setTitleFont(incomeTitleFont);
        EmptyBorder incomeBorder = new EmptyBorder(50, 50, 50, 50);
        incomePanel.setBorder(BorderFactory.createCompoundBorder(incomeTitleBorder, incomeBorder));
        tabbedPane.addTab("Income", incomePanel);
        tabbedPane.setBackgroundAt(1, Color.PINK);

        incomeDetailPanel = new JPanel();
        incomeDetailPanel.setLayout(new BorderLayout());

        // Making a card layout for the income tab
        CardLayout cardLayout1 = new CardLayout();
        incomeDetailPanel.setLayout(cardLayout1);
        incomePanel.add(incomeDetailPanel, BorderLayout.CENTER);

        // First a blank screen shows up
        final String BLANK_PANEL1 = "blank";
        JPanel blankPanel = new JPanel();
        blankPanel.setBackground(Color.GRAY);
        incomeDetailPanel.add(blankPanel, BLANK_PANEL1);

        // Creating another panel for when the user goes to the "add income (per month)" button
        final String INCOME_PANEL = "income";
        JPanel salaryPanel = new JPanel();
        salaryPanel.setBackground(Color.PINK);
        salaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        salaryPanel.add(new JLabel("Salary:"));
        inputSalary = new JTextField(8);
        setupNumericInputFilter(inputSalary); // Implementing method that only allows numeric inputs
        salaryPanel.add(inputSalary);
        salaryPanel.add(new JLabel("€"));
        salaryPanel.add(new JLabel("Benefits:"));
        inputBenefits = new JTextField(8);
        setupNumericInputFilter(inputBenefits);
        salaryPanel.add(inputBenefits);
        salaryPanel.add(new JLabel("€"));
        salaryPanel.add(new JLabel("Other:"));
        inputOtherIncome = new JTextField(8);
        setupNumericInputFilter(inputOtherIncome);
        salaryPanel.add(inputOtherIncome);
        salaryPanel.add(new JLabel("€"));
        incomeDetailPanel.add(salaryPanel, INCOME_PANEL);

        incomeButtonPanel = new JPanel();
        incomeButtonPanel.setLayout(new BorderLayout());
        incomePanel.add(incomeButtonPanel, BorderLayout.NORTH);
        incomePanel.add(incomeButtonPanel, BorderLayout.NORTH);

        // Creating the "add income (per month)" button
        JButton salaryButton = new JButton("Add income (per month)");
        salaryButton.setBackground(Color.PINK);
        salaryButton.addActionListener((ActionEvent e) ->
                cardLayout1.show(incomeDetailPanel, INCOME_PANEL));
        incomeButtonPanel.add(salaryButton, BorderLayout.LINE_START);

        frame.setVisible(true);

        // The expenses panel and tab
        expensesPanel = new JPanel();
        expensesPanel.setLayout(new BorderLayout());
        TitledBorder expenseTitleBorder = BorderFactory.createTitledBorder("EXPENSES");
        Font expenseTitleFont = new Font("Arial", Font.BOLD, 18);
        expenseTitleBorder.setTitleFont(expenseTitleFont);
        EmptyBorder expenseBorder = new EmptyBorder(50, 50, 50, 50);
        expensesPanel.setBorder(BorderFactory.createCompoundBorder(expenseTitleBorder, expenseBorder));
        tabbedPane.addTab("Expenses", expensesPanel);
        tabbedPane.setBackgroundAt(2, Color.PINK);

        expenseButtonPanel = new JPanel();
        expenseButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        expensesPanel.add(expenseButtonPanel, BorderLayout.NORTH);

        expenseDetailPanel = new JPanel();
        expenseDetailPanel.setLayout(new BorderLayout());

        // Making a card layout for the expenses tab
        CardLayout expenseCardLayout = new CardLayout();
        expenseDetailPanel.setLayout(expenseCardLayout);
        expensesPanel.add(expenseDetailPanel, BorderLayout.CENTER);

        // First a blank screen shows up
        final String BLANK_PANEL2 = "blank";
        JPanel blankPanel2 = new JPanel();
        blankPanel.setBackground(Color.WHITE); // setting the background color to white
        expenseDetailPanel.add(blankPanel2, BLANK_PANEL2);

        // Creating another panel for when the user goes to the "necessities" button
        final String NECESSITIES_PANEL = "necessities";
        JPanel necessitiesPanel = new JPanel();
        necessitiesPanel.setBackground(Color.PINK); // setting the background color to pink
        necessitiesPanel.setLayout(new GridLayout(0, 1));

        // Creating a panel that contains the rent and insurance information
        JPanel rentInsurancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Applying a flow layout
        rentInsurancePanel.setBackground(Color.PINK);
        rentInsurancePanel.add(new JLabel("Rent:"));
        inputRent = new JTextField(8); // Creating a text box for rent and setting its width
        setupNumericInputFilter(inputRent); // Implementing method that only allows numeric inputs
        rentInsurancePanel.add(inputRent);
        rentInsurancePanel.add(new JLabel("€"));
        rentInsurancePanel.add(new JLabel("Insurance:"));
        inputInsurance = new JTextField(8); // Creating a text box for insurance and setting its width
        setupNumericInputFilter(inputInsurance);
        rentInsurancePanel.add(inputInsurance);
        rentInsurancePanel.add(new JLabel("€"));
        necessitiesPanel.add(rentInsurancePanel);

        // Creating a panel that contains a combo box that allows the selection of a day
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePanel.add(new JLabel("Date of month:"));
        String[] daysOfMonth = new String[31];
        for (int i = 1; i <= 31; i++) {
            daysOfMonth[i - 1] = String.valueOf(i);
        }
        JComboBox<String> dayComboBox = new JComboBox<>(daysOfMonth);
        datePanel.add(dayComboBox);
        necessitiesPanel.add(datePanel);

        // Creating a panel that contains the daily and total food cost information
        JPanel foodExpensesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        foodExpensesPanel.setBackground(Color.PINK);
        JLabel foodInputLabel = new JLabel("Daily food costs ");
        foodExpensesPanel.add(foodInputLabel);
        JTextField inputFood = new JTextField(8);
        setupNumericInputFilter(inputFood);
        foodExpensesPanel.add(inputFood);
        JButton sumFoodButton = new JButton("Add");
        foodExpensesPanel.add(sumFoodButton);
        foodExpensesPanel.add(new JLabel("Total food expenses:"));
        totalFood = new JTextField(8);
        foodExpensesPanel.add(totalFood);
        totalFood.setEditable(false);
        foodExpensesPanel.add(new JLabel("€"));
        necessitiesPanel.add(foodExpensesPanel);

        // Adding an ActionListener to the "sum" button that adds the daily food expenses together
        sumFoodButton.addActionListener(e -> {
            double totalFoodExpenses = 0.0;
            for (double foodExpense : foodExpenses) {
                totalFoodExpenses += foodExpense;
            }
            totalFood.setText(String.format("%s", totalFoodExpenses));
        });

        // Creating a panel that contains the daily and total transportation cost information
        JPanel transportExpensesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        transportExpensesPanel.setBackground(Color.PINK);
        JLabel transportInputLabel = new JLabel("Daily transportation costs ");
        transportExpensesPanel.add(transportInputLabel);
        JTextField inputTransport = new JTextField(8);
        setupNumericInputFilter(inputTransport);
        transportExpensesPanel.add(inputTransport);
        JButton sumTransportButton = new JButton("Add");
        transportExpensesPanel.add(sumTransportButton);
        transportExpensesPanel.add(new JLabel("Total transportation costs:"));
        totalTransport = new JTextField(8);
        transportExpensesPanel.add(totalTransport);
        totalTransport.setEditable(false);
        transportExpensesPanel.add(new JLabel("€"));
        necessitiesPanel.add(transportExpensesPanel);

        // Adding an ActionListener to the "sum" button
        sumTransportButton.addActionListener(e -> {
            double totalTransportExpenses = 0.0;
            for (double transportExpense : transportExpenses) {
                totalTransportExpenses += transportExpense;
            }
            totalTransport.setText(String.format("%s", totalTransportExpenses));
        });

        // Creating a panel that contains the daily and total other cost information
        JPanel otherExpensesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        otherExpensesPanel.setBackground(Color.PINK);
        JLabel otherInputLabel = new JLabel("Other daily costs ");
        otherExpensesPanel.add(otherInputLabel);
        JTextField inputOtherExpenses = new JTextField(8);
        setupNumericInputFilter(inputOtherExpenses);
        otherExpensesPanel.add(inputOtherExpenses);
        JButton sumOthersButton = new JButton("Add");
        otherExpensesPanel.add(sumOthersButton);
        otherExpensesPanel.add(new JLabel("Total other costs:"));
        totalOther = new JTextField(8);
        otherExpensesPanel.add(totalOther);
        totalOther.setEditable(false);
        otherExpensesPanel.add(new JLabel("€"));
        necessitiesPanel.add(otherExpensesPanel);

        // Add an ActionListener to the "sum" button
        sumOthersButton.addActionListener(e -> {
            double totalOtherExpenses = 0.0;
            for (double otherExpense : otherExpenses) {
                totalOtherExpenses += otherExpense;
            }
            totalOther.setText(String.format("%s", totalOtherExpenses));
        });

        // Creating a KeyAdapter for the food input field in case of changes in a selected day by the user
        KeyAdapter foodInputKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String selectedDay = (String) dayComboBox.getSelectedItem(); // Retrieve the selected day
                if (selectedDay != null) {
                    int day = Integer.parseInt(selectedDay) - 1;  // Convert the selected day to an index
                    double value = parseTextField(inputFood); // Obtain the value from the inputFood text box
                    foodExpenses[day] = value; // Update array at the index corresponding to the selected day
                }
            }
        };

        // Doing the same thing for the transportation costs
        KeyAdapter transportInputKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String selectedDay = (String) dayComboBox.getSelectedItem();
                if (selectedDay != null) {
                    int day = Integer.parseInt(selectedDay) - 1;
                    double value = parseTextField(inputTransport);
                    transportExpenses[day] = value;
                }
            }
        };

        // Doing the same thing for other costs
        KeyAdapter otherExpensesInputKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String selectedDay = (String) dayComboBox.getSelectedItem();
                if (selectedDay != null) {
                    int day = Integer.parseInt(selectedDay) - 1;
                    double value = parseTextField(inputOtherExpenses);
                    otherExpenses[day] = value;
                }
            }
        };

        // Adjust the UI to display the expense information based on the selected day
        dayComboBox.addActionListener(e -> {
            String selectedDay = (String) dayComboBox.getSelectedItem();
            if (selectedDay != null) {
                int day = Integer.parseInt(selectedDay) - 1;
                inputFood.setVisible(true);
                foodInputLabel.setText("Food for day " + selectedDay + ": ");
                inputFood.setText(String.valueOf(foodExpenses[day]));
                inputFood.addKeyListener(foodInputKeyListener);

                inputTransport.setVisible(true);
                transportInputLabel.setText("Transportation for day " + selectedDay + ": ");
                inputTransport.setText(String.valueOf(transportExpenses[day]));
                inputTransport.addKeyListener(transportInputKeyListener);

                inputOtherExpenses.setVisible(true);
                otherInputLabel.setText("Other expenses for day " + selectedDay + ": ");
                inputOtherExpenses.setText(String.valueOf(otherExpenses[day]));
                inputOtherExpenses.addKeyListener(otherExpensesInputKeyListener);
            }
        });

        expenseDetailPanel.add(necessitiesPanel, NECESSITIES_PANEL);

        // Creating another panel for when the user goes to the supplementary button
        final String SUPPLEMENTARY_PANEL = "supplementary";
        JPanel supplementaryPanel = new JPanel();
        supplementaryPanel.setBackground(Color.PINK);
        supplementaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        supplementaryPanel.add(new JLabel("Entertainment:"));
        inputEntertainment = new JTextField(8);
        setupNumericInputFilter(inputEntertainment);
        supplementaryPanel.add(inputEntertainment);
        supplementaryPanel.add(new JLabel("€"));
        supplementaryPanel.add(new JLabel("Clothes:"));
        inputClothes = new JTextField(8);
        setupNumericInputFilter(inputClothes);
        supplementaryPanel.add(inputClothes);
        supplementaryPanel.add(new JLabel("€"));
        supplementaryPanel.add(new JLabel("Other:"));
        inputOtherSup = new JTextField(8);
        setupNumericInputFilter(inputOtherSup);
        supplementaryPanel.add(inputOtherSup);
        supplementaryPanel.add(new JLabel("€"));
        expenseDetailPanel.add(supplementaryPanel, SUPPLEMENTARY_PANEL);

        // When user clicks on the "necessities" button, its panel opens
        JButton necessitiesButton = new JButton("Necessities");
        necessitiesButton.setBackground(Color.PINK);
        necessitiesButton.addActionListener((ActionEvent e) ->
                expenseCardLayout.show(expenseDetailPanel, NECESSITIES_PANEL));
        expenseButtonPanel.add(necessitiesButton, BorderLayout.LINE_START);

        // When user clicks on the "supplementary" button, its panel opens
        JButton supplementaryButton = new JButton("Supplementary");
        supplementaryButton.setBackground(Color.PINK);
        supplementaryButton.addActionListener((ActionEvent e) ->
                expenseCardLayout.show(expenseDetailPanel, SUPPLEMENTARY_PANEL));
        expenseButtonPanel.add(supplementaryButton, BorderLayout.LINE_END);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // Creating a save income button
        JButton saveIncomeButton = new JButton("Save");
        saveIncomeButton.setBackground(Color.LIGHT_GRAY);
        incomeButtonPanel.add(saveIncomeButton, BorderLayout.LINE_END);

        // Adding an ActionListener to the save button
        saveIncomeButton.addActionListener((ActionEvent e) -> {
            // Adding the inputs from the text boxes in the "income" tab together:
            totalIncome = parseTextField(inputSalary) + parseTextField(inputBenefits)
                    + parseTextField(inputOtherIncome);
            incomeTextBox.setText(String.format("%s", totalIncome)); // Display total

            // Adding the inputs from all the text boxes in the "expenses" tab together:
            totalExpenses = parseTextField(totalFood) + parseTextField(inputRent)
                    + parseTextField(inputInsurance) + parseTextField(totalTransport)
                    + parseTextField(inputEntertainment) + parseTextField(inputClothes)
                    + parseTextField(totalOther) + parseTextField(inputOtherSup);
            expensesTextBox.setText(String.format("%s", totalExpenses)); // Display total

            // Calculating total savings/losses
            double totalSavings = totalIncome - totalExpenses;
            savingsTextBox.setText(String.format("%s", totalSavings)); // Display total

            // Get the FinancialData object for the selected month and update its values.
            // The monthData map from the main method is used to connect to the correct
            // FinancialData object.
            FinancialData data = monthData.get(selectedMonth);
            data.setIncome(totalIncome);
            data.setExpenses(totalExpenses);
            data.setSavings(totalSavings);

            // Display a message
            JOptionPane.showMessageDialog(frame, "Income data saved successfully.",
                    "Save Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Creating a save expenses button
        JButton saveExpensesButton = new JButton("Save");
        saveExpensesButton.setBackground(Color.LIGHT_GRAY);
        expenseButtonPanel.add(saveExpensesButton, BorderLayout.LINE_END);

        saveExpensesButton.addActionListener((ActionEvent e) -> {
            // Retrieve the user inputs:
            double totalFoodExpenses = parseTextField(totalFood);
            double totalTransportExpenses = parseTextField(totalTransport);
            double totalOtherExpenses = parseTextField(totalOther);
            double totalRent = parseTextField(inputRent);
            double totalInsurance = parseTextField(inputInsurance);
            double totalEntertainment = parseTextField(inputEntertainment);
            double totalClothes = parseTextField(inputClothes);
            double totalOtherSup = parseTextField(inputOtherSup);

            // Calculate total expenses
            double totalExpenses = totalFoodExpenses + totalRent + totalInsurance + totalTransportExpenses
                    + totalEntertainment + totalClothes + totalOtherExpenses + totalOtherSup;

            // Updates the "total expenses" text box in the main menu
            expensesTextBox.setText(String.format("%s", totalExpenses));

            // Total income is calculated
            double totalIncome = parseTextField(inputSalary) + parseTextField(inputBenefits)
                    + parseTextField(inputOtherIncome);

            // Calculating total savings/losses
            double totalSavings = totalIncome - totalExpenses;

            // Get the FinancialData object for the selected month and update its values. The monthData map
            // from the main method is used to connect to the correct FinancialData object.
            FinancialData data = monthData.get(selectedMonth);
            data.setIncome(totalIncome);
            data.setExpenses(totalExpenses);
            data.setSavings(totalSavings);

            // Updates the "total expenses" text box in the main menu
            savingsTextBox.setText(String.format("%s", totalSavings));

            // Updates and displays both pie charts side by side
            updateBothPieCharts(totalFoodExpenses, totalRent, totalInsurance, totalTransportExpenses,
                    totalOtherExpenses, totalEntertainment, totalClothes, totalOtherSup);

            // Display a message
            JOptionPane.showMessageDialog(frame, "Expense data saved successfully.", "Save Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Creating the panel and tab for things to save up for
        savingUpForPanel = new JPanel();
        savingUpForPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        TitledBorder savingForTitleBorder = BorderFactory.createTitledBorder("SAVING UP FOR");
        Font savingForFont = new Font("Arial", Font.BOLD, 18);
        savingForTitleBorder.setTitleFont(savingForFont);
        EmptyBorder savingForBorder = new EmptyBorder(50, 50, 50, 50);
        savingUpForPanel.setBorder(BorderFactory.createCompoundBorder(savingForTitleBorder, savingForBorder));
        tabbedPane.addTab("Saving up for", savingUpForPanel);
        tabbedPane.setBackgroundAt(3, Color.PINK);

        JTextArea saveForArea = new JTextArea(10, 10); // Text area for displaying items
        saveForArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(saveForArea);

        JButton addItemButton = new JButton("Add Item"); // Button for adding new items
        addItemButton.setBackground(Color.PINK);

        savingUpForPanel.setLayout(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());

        listPanel.add(addItemButton, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        savingUpForPanel.add(listPanel, BorderLayout.NORTH);

        // Creating a list to display the items
        saveForListModel = new DefaultListModel<>();
        saveForList = new JList<>(saveForListModel);

        JScrollPane listScrollPane = new JScrollPane(saveForList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        // Creating the dialog which opens when the "add item" button is pressed
        addItemButton.addActionListener((ActionEvent e) -> {
            JDialog customDialog = new JDialog();
            customDialog.setTitle("Add Item with Price");
            customDialog.setModal(true);
            customDialog.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JTextField itemNameField = new JTextField(15);
            JTextField itemPriceField = new JTextField(10);
            setupNumericInputFilter(itemPriceField);
            inputPanel.add(new JLabel("Item Name:"));
            inputPanel.add(itemNameField);
            inputPanel.add(new JLabel("Price:"));
            inputPanel.add(itemPriceField);
            inputPanel.add(new JLabel("€"));

            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            // When OK is pressed, a formatted string to the saveForListModel
            okButton.addActionListener((ActionEvent event) -> {
                String itemName = itemNameField.getText();
                String itemPrice = itemPriceField.getText();
                if (!itemName.isEmpty() && !itemPrice.isEmpty()) {
                    saveForListModel.addElement("• " + itemName + " (" + itemPrice + "€)");
                }
                customDialog.dispose();
            });

            cancelButton.addActionListener((ActionEvent event) -> customDialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            customDialog.add(inputPanel, BorderLayout.CENTER);
            customDialog.add(buttonPanel, BorderLayout.SOUTH);
            customDialog.pack();

            customDialog.setLocationRelativeTo(null);
            customDialog.setVisible(true);
        });

        // Deletion of an item:
        saveForList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = saveForList.getSelectedIndex();
                if (selectedIndex != -1) { // Check if an item has been selected
                    int confirm = JOptionPane.showConfirmDialog(
                            savingUpForPanel,
                            "Do you want to remove the selected item?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) { // User confirmed to remove the item
                        // Remove the selected item from the list model
                        saveForListModel.remove(selectedIndex);
                    }
                }
            }
        });

        // Implementing persistent data i.e. the save and load methods
        loadDataFromFile();
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveDataToFile));
    }

    // Method to only allow numerical value input
    private void setupNumericInputFilter(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
    }

    // Method to obtain the value in a text field
    private double parseTextField(JTextField textField) {
        try {
            // Get the text inputted by the user into the text field
            String text = textField.getText();
            // Attempt to parse the text into a double
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0.0; // In the case that parsing fails
        }
    }

    // Method to save data to a month's file
    private void saveDataToFile() {
        // Constructing a FileWriter that writes user inputs to a month's file
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFileName))) {
            // Writing i.e. user inputs to the file
            writer.println(inputSalary.getText());
            writer.println(inputBenefits.getText());
            writer.println(inputOtherIncome.getText());
            writer.println(totalFood.getText());
            // Loop through the arrays to save each day's expenses
            for (double foodExpense : foodExpenses) {
                writer.println(foodExpense);
            }
            for (double transportExpense : transportExpenses) {
                writer.println(transportExpense);
            }
            for (double otherExpense : otherExpenses) {
                writer.println(otherExpense);
            }
            writer.println(inputRent.getText());
            writer.println(inputInsurance.getText());
            writer.println(totalTransport.getText());
            writer.println(inputEntertainment.getText());
            writer.println(inputClothes.getText());
            writer.println(inputOtherSup.getText());
            writer.println(totalOther.getText());
            writer.println(incomeTextBox.getText());
            writer.println(expensesTextBox.getText());
            writer.println(savingsTextBox.getText());
            // Writing i.e. saving the list of items in the "saving up for" tab:
            for (int i = 0; i < saveForListModel.size(); i++) {
                writer.println(saveForListModel.getElementAt(i));
            }
        } catch (IOException e) {
            // Handle file writing errors (e.g. file not found)
            e.printStackTrace();
            JDialog errorDialog = new JDialog(); // Dialog to display the message on
            errorDialog.setSize(400, 400);
            // Display message:
            JLabel errorLabel = new JLabel("File saving error:" + e.getMessage());
            errorDialog.add(errorLabel);
            errorDialog.setVisible(true);
        }
    }

    // Method to load data from file
    private void loadDataFromFile() {
        // Constructing a BufferedReader to read from a month's file
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFileName))) {
            // Read i.e. load user inputs from the file
            inputSalary.setText(reader.readLine());
            inputBenefits.setText(reader.readLine());
            inputOtherIncome.setText(reader.readLine());
            totalFood.setText(reader.readLine());
            // Pass through all the arrays to get the values for all days
            for (int i = 0; i < foodExpenses.length; i++) {
                String foodExpense = reader.readLine();
                // If there are no inputs then the value in the text box has to be changed
                // to a double because the array is stored as type double.
                if (foodExpense != null && !foodExpense.isEmpty()) {
                    foodExpenses[i] = Double.parseDouble(foodExpense);
                }
            }
            for (int i = 0; i < transportExpenses.length; i++) {
                String transportExpense = reader.readLine();
                if (transportExpense != null && !transportExpense.isEmpty()) {
                    transportExpenses[i] = Double.parseDouble(transportExpense);
                }
            }
            for (int i = 0; i < otherExpenses.length; i++) {
                String otherExpense = reader.readLine();
                if (otherExpense != null && !otherExpense.isEmpty()) {
                    otherExpenses[i] = Double.parseDouble(otherExpense);
                }
            }
            inputRent.setText(reader.readLine());
            inputInsurance.setText(reader.readLine());
            totalTransport.setText(reader.readLine());
            inputEntertainment.setText(reader.readLine());
            inputClothes.setText(reader.readLine());
            inputOtherSup.setText(reader.readLine());
            totalOther.setText(reader.readLine());
            incomeTextBox.setText(reader.readLine());
            expensesTextBox.setText(reader.readLine());
            savingsTextBox.setText(reader.readLine());

            String line;
            // Create an ArrayList to store lines read (from the "saving up for" tab) from the file
            ArrayList<String> savedItems = new ArrayList<>();
            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                // Add each line to the ArrayList
                savedItems.add(line);
            }
            // Load and display the items that the user is saving up for
            savedItems.forEach(saveForListModel::addElement);

        } catch (IOException e) {
            // Handle file loading errors (e.g. file not found)
            e.printStackTrace();
            JDialog errorDialog = new JDialog(); // Dialog to display the message on
            errorDialog.setSize(400, 400);
            // Display message:
            JLabel errorLabel = new JLabel("File loading error:" + e.getMessage());
            errorDialog.add(errorLabel);
            errorDialog.setVisible(true);
        }
    }

    // Method creating a bar chart comparing total income and total expenses
    private void showBarChart(double totalIncome, double totalExpenses) {
        // Creating a dataset to store bar chart data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Adding values to the datasets i.e. the calculated "totalIncome", "totalExpenses"
        dataset.addValue(totalIncome, "Income", "Total Income");
        dataset.addValue(totalExpenses, "Expenses", "Total Expenses");

        // Create a bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Income vs. Expenses",
                "Category",
                "Amount (€)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        // Setting the range of the axis to be from 0 to the maximum value among
        // totalIncome and totalExpenses, plus 100
        numberAxis.setRange(0, Math.max(totalIncome, totalExpenses) + 100);

        // Customizing the appearance
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.1);
        renderer.setSeriesPaint(0, new Color(255, 175, 175)); // Setting custom colors
        renderer.setSeriesPaint(1, new Color(255, 100, 100));

        ChartPanel chartPanel = new ChartPanel(barChart); // Creating a panel to display the chart

        // Creating a frame to contain the chart panel
        JFrame chartFrame = new JFrame("Income vs Expenses");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    // Method creating two pie charts: one comparing all expenses, and one
    // comparing supplementary with necessities
    private void updateBothPieCharts(double food, double rent, double insurance, double transportation,
                                     double otherExpenses, double entertainment, double clothes,
                                     double other) {

        // Creating the "all expenses" pie chart
        DefaultPieDataset allExpensesDataset = createAllExpensesPieDataset(food, rent, insurance,
                transportation, otherExpenses, entertainment, clothes, other); // Using the
        // createAllExpensesPieDataset method
        JFreeChart allExpensesPieChart = ChartFactory.createPieChart(
                "All Expenses",
                allExpensesDataset,
                true, true, false);

        PiePlot plot1 = (PiePlot) allExpensesPieChart.getPlot();

        // Assigning different colors for each different section of the pie chart
        plot1.setSectionPaint("Food", new Color(255, 105, 180));
        plot1.setSectionPaint("Rent", new Color(79, 28, 45));
        plot1.setSectionPaint("Insurance", new Color(212, 169, 175));
        plot1.setSectionPaint("Transportation", new Color(245, 66, 123));
        plot1.setSectionPaint("Other necessities", new Color(150, 75, 123));
        plot1.setSectionPaint("Entertainment", new Color(220, 220, 220));
        plot1.setSectionPaint("Clothes", new Color(148, 96, 105));
        plot1.setSectionPaint("Other supplementary cost", new Color(255, 212, 226));

        // Creating the "supplementary vs. necessary Expenses" pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Necessary Expenses", food + rent + insurance + transportation + otherExpenses);
        dataset.setValue("Supplementary Expenses", entertainment + clothes + other);

        JFreeChart supNecExpensesPieChart = ChartFactory.createPieChart(
                "Necessity expenses vs. supplementary expenses",
                dataset,
                true, true, false);

        PiePlot plot2 = (PiePlot) supNecExpensesPieChart.getPlot();

        // Assigning different colors to necessary and supplementary sections
        plot2.setSectionPaint("Necessary Expenses", new Color(255, 105, 180));
        plot2.setSectionPaint("Supplementary Expenses", new Color(255, 182, 193));

        // Creating panels for both pie charts
        ChartPanel allExpensesChartPanel = new ChartPanel(allExpensesPieChart);
        ChartPanel supNecExpensesChartPanel = new ChartPanel(supNecExpensesPieChart);

        // Creating a panel to hold both pie charts side by side
        JPanel pieChartsPanel = new JPanel(new GridLayout(1, 2));
        pieChartsPanel.add(allExpensesChartPanel);
        pieChartsPanel.add(supNecExpensesChartPanel);

        // Creating a frame to display the pie charts
        JFrame pieChartsFrame = new JFrame("Expense Pie Charts");
        pieChartsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pieChartsFrame.add(pieChartsPanel);
        pieChartsFrame.pack();
        pieChartsFrame.setVisible(true);
    }

    // Method creating a data set of all the different expense categories
    private DefaultPieDataset createAllExpensesPieDataset(double food, double rent, double insurance,
                                                          double transportation, double otherExpenses,
                                                          double entertainment, double clothes, double other) {

        DefaultPieDataset dataset = new DefaultPieDataset();

        dataset.setValue("Food", food);
        dataset.setValue("Rent", rent);
        dataset.setValue("Insurance", insurance);
        dataset.setValue("Transportation", transportation);
        dataset.setValue("Other necessities", otherExpenses);
        dataset.setValue("Entertainment", entertainment);
        dataset.setValue("Clothes", clothes);
        dataset.setValue("Other supplementary cost", other);

        return dataset;
    }
}
