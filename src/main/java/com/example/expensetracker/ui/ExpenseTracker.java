package com.example.expensetracker.ui;

import com.example.expensetracker.dao.ExpenseDAO;
import com.example.expensetracker.db.MongoConnection;
import com.example.expensetracker.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * ExpenseTracker - Main UI class for the Expense Tracker application.
 * This class provides a comprehensive user interface for managing personal expenses.
 * Features include:
 * - Adding new expenses with amount, category, date, and description
 * - Viewing all expenses in a formatted table
 * - Real-time calculation and display of total expenses
 * - Modern, user-friendly interface with professional styling
 * The application uses MongoDB for persistent storage through the ExpenseDAO layer.
 *
 * @author Your Name
 * @version 1.0
 */
public class ExpenseTracker extends JFrame {

    // ========== Constants ==========

    /** Primary brand color for buttons and accents */
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);

    /** Primary hover color for interactive elements */
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);

    /** Background color for the main frame */
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);

    /** Alternate row color for table striping */
    private static final Color ROW_ALTERNATE = new Color(240, 242, 245);

    /** Border color for input fields */
    private static final Color BORDER_COLOR = new Color(206, 212, 218);

    /** Header background color */
    private static final Color HEADER_COLOR = new Color(52, 58, 64);

    /** Success/positive color */
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    // ========== Instance Variables ==========

    /** Data Access Object for performing CRUD operations on expenses */
    private final ExpenseDAO dao = new ExpenseDAO();

    /** Custom table model that manages the expense data displayed in the JTable */
    private final ExpenseTableModel tableModel = new ExpenseTableModel();

    /** Label displaying the sum of all expenses */
    private final JLabel totalLabel = new JLabel("Total: ₹0.00");

    // ========== Constructor ==========

    /**
     * Constructs the main ExpenseTracker window.
     * Initializes all UI components, sets up the layout, and loads existing expenses.
     */
    public ExpenseTracker() {
        try {
            // Configure main frame properties
            setTitle("Expense Tracker - Manage Your Finances");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 600);
            setMinimumSize(new Dimension(800, 500));
            setLocationRelativeTo(null);

            // Initialize and assemble UI components
            JPanel topPanel = createInputPanel();
            JTable table = createExpenseTable();
            JPanel bottomPanel = createTotalPanel();

            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(BACKGROUND_COLOR);

            add(topPanel, BorderLayout.NORTH);
            add(createTablePanel(table), BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            // Load and display existing expenses from database
            refreshTable();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
//    public ExpenseTracker() {
//        // Configure main frame properties
//        setTitle("Expense Tracker - Manage Your Finances");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(900, 600);
//        setMinimumSize(new Dimension(800, 500));
//        setLocationRelativeTo(null); // Center on screen
//
//        // Set application icon (optional - requires icon file)
//        // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
//
//        // Initialize and assemble UI components
//        JPanel topPanel = createInputPanel();
//        JTable table = createExpenseTable();
//        JPanel bottomPanel = createTotalPanel();
//
//        // Set main frame layout and background
//        setLayout(new BorderLayout(10, 10));
//        getContentPane().setBackground(BACKGROUND_COLOR);
//
//        // Add components to frame with proper spacing
//        add(topPanel, BorderLayout.NORTH);
//        add(createTablePanel(table), BorderLayout.CENTER);
//        add(bottomPanel, BorderLayout.SOUTH);
//
//        // Load and display existing expenses from database
//        refreshTable();
//    }

    // ========== UI Component Creation Methods ==========

    /**
     * Creates the top input panel containing all form fields for adding a new expense.
     *
     * The panel includes:
     * - Amount input field (numeric)
     * - Category dropdown (predefined categories)
     * - Date selector (calendar spinner)
     * - Description text area (multi-line)
     * - Add button to submit the form
     *
     * @return JPanel configured with input fields and labels
     */
    private JPanel createInputPanel() {
        // Main panel with grid bag layout for flexible positioning
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 20, 20, 20),
                BorderFactory.createTitledBorder(
                        new LineBorder(BORDER_COLOR, 1, true),
                        "Add New Expense",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        HEADER_COLOR
                )
        ));
        panel.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8); // Spacing between components
        c.fill = GridBagConstraints.HORIZONTAL;

        // ========== Input Components ==========

        // Amount field - accepts decimal numbers
        JTextField amountField = new JTextField(15);
        styleTextField(amountField);
        amountField.setToolTipText("Enter the expense amount (e.g., 150.50)");

        // Category dropdown - predefined expense categories
        JComboBox<String> categoryBox = new JComboBox<>(
                new String[]{"Food", "Transport", "Shopping", "Bills", "Entertainment", "Healthcare", "Other"}
        );
        styleComboBox(categoryBox);
        categoryBox.setToolTipText("Select the expense category");

        // Date spinner - allows date selection with formatted display
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);
        dateSpinner.setToolTipText("Select the date of expense");

        // Description area - multi-line text input for additional details
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        descArea.setToolTipText("Enter a description for this expense (optional)");
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        // Add button - submits the expense form
        JButton addBtn = new JButton("Add Expense");
        styleButton(addBtn);
        addBtn.setToolTipText("Click to save this expense");

        // ========== Layout Configuration ==========

        // Row 0: Amount field
        c.gridx = 0; c.gridy = 0; c.weightx = 0.2;
        panel.add(createStyledLabel("Amount (₹):"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 0.8;
        panel.add(amountField, c);

        // Row 1: Category dropdown
        c.gridx = 0; c.gridy = 1; c.weightx = 0.2;
        panel.add(createStyledLabel("Category:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 0.8;
        panel.add(categoryBox, c);

        // Row 2: Date spinner
        c.gridx = 0; c.gridy = 2;
        panel.add(createStyledLabel("Date:"), c);
        c.gridx = 1; c.gridy = 2;
        panel.add(dateSpinner, c);

        // Row 3: Description area
        c.gridx = 0; c.gridy = 3;
        panel.add(createStyledLabel("Description:"), c);
        c.gridx = 1; c.gridy = 3;
        panel.add(descScroll, c);

        // Row 4: Add button (right-aligned)
        c.gridx = 1; c.gridy = 4;
        c.anchor = GridBagConstraints.EAST;
        panel.add(addBtn, c);

        // ========== Event Handling ==========

        /*
         * Add button action listener.
         * Validates input, creates an Expense object, saves to database,
         * refreshes the table, and resets the form.
         */
        addBtn.addActionListener(e -> {
            try {
                // Validate and parse amount
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    showError("Amount cannot be empty");
                    return;
                }
                double amount = Double.parseDouble(amountText);

                // Validate amount is positive
                if (amount <= 0) {
                    showError("Amount must be greater than zero");
                    return;
                }

                // Extract other form values
                String category = (String) categoryBox.getSelectedItem();
                String description = descArea.getText().trim();
                Date date = (Date) dateSpinner.getValue();

                // Create and save expense object
                Expense expense = new Expense(amount, category, description, date);
                dao.insertExpense(expense);

                // Update UI
                refreshTable();

                // Reset form fields
                amountField.setText("");
                descArea.setText("");
                categoryBox.setSelectedIndex(0);
                dateSpinner.setValue(new Date());

                // Show success feedback
                showSuccess("Expense added successfully!");

            } catch (NumberFormatException ex) {
                showError("Please enter a valid numeric amount");
            } catch (Exception ex) {
                showError("Failed to add expense: " + ex.getMessage());
            }
        });

        return panel;
    }

    /**
     * Creates a wrapper panel for the expense table with proper styling.
     *
     * @param table The JTable to be wrapped
     * @return JPanel containing the table in a scroll pane
     */
    private JPanel createTablePanel(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(BORDER_COLOR, 1, true),
                        "Expense History",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        HEADER_COLOR
                ),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates and configures the JTable for displaying expenses.
     *
     * Styling includes:
     * - Custom fonts and colors
     * - Alternating row colors for readability
     * - Center-aligned amount column
     * - Professional header styling
     * - Optimal row height and spacing
     *
     * @return Configured JTable instance
     */
    private JTable createExpenseTable() {
        JTable table = new JTable(tableModel);

        // Basic table configuration
        table.setFillsViewportHeight(true);
        table.setRowHeight(32); // Comfortable row height
        table.setShowGrid(false); // Cleaner look without grid lines
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(new LineBorder(HEADER_COLOR, 1));

        // Center-align the Amount column for better readability
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        if (table.getColumnCount() > 3) {
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }

        /*
         * Custom cell renderer for alternating row colors.
         * Provides visual separation between rows without grid lines.
         */
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);

                // Apply alternating row colors when not selected
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALTERNATE);
                    c.setForeground(Color.BLACK);
                }

                // Add padding to cells
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(5, 10, 5, 10));
                }

                return c;
            }
        });

        return table;
    }

    /**
     * Creates the bottom panel displaying the total sum of all expenses.
     *
     * The panel includes:
     * - Total amount label with formatted currency
     * - Professional styling with emphasis
     * - Right-aligned for natural reading flow
     *
     * @return JPanel containing the total expenses label
     */
    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        // Style the total label with emphasis
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(SUCCESS_COLOR);

        panel.add(totalLabel);
        return panel;
    }

    // ========== Data Management Methods ==========

    /**
     * Refreshes the table display and total amount from the database.
     *
     * This method:
     * 1. Fetches all expenses from MongoDB via DAO
     * 2. Updates the table model with the retrieved data
     * 3. Recalculates and updates the total amount label
     *
     * Called after adding a new expense or on initial load.
     */
    private void refreshTable() {
        // Retrieve all expenses from database
        List<Expense> expenses = dao.getAllExpenses();

        // Update table model with fresh data
        tableModel.setItems(expenses);

        // Calculate total using stream API
        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Update total label with formatted currency
        totalLabel.setText(String.format("Total: ₹%.2f", total));
    }

    // ========== Styling Helper Methods ==========

    /**
     * Applies consistent styling to buttons throughout the application.
     *
     * @param button The JButton to style
     */
    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for better interactivity
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    /**
     * Applies consistent styling to text fields.
     *
     * @param textField The JTextField to style
     */
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    /**
     * Applies consistent styling to combo boxes.
     *
     * @param comboBox The JComboBox to style
     */
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Applies consistent styling to spinners.
     *
     * @param spinner The JSpinner to style
     */
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    /**
     * Creates a styled label for form fields.
     *
     * @param text The label text
     * @return Styled JLabel
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(HEADER_COLOR);
        return label;
    }

    // ========== User Feedback Methods ==========

    /**
     * Displays an error message dialog to the user.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a success message dialog to the user.
     *
     * @param message The success message to display
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========== Application Entry Point ==========

    /**
     * Main method - Entry point for the Expense Tracker application.
     *
     * This method:
     * 1. Launches the application on the Event Dispatch Thread (EDT)
     * 2. Registers a shutdown hook to properly close MongoDB connection
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set system look and feel for native appearance (optional)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }

        // Launch UI on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            ExpenseTracker tracker = new ExpenseTracker();
            tracker.setVisible(true);
        });

        // Ensure MongoDB connection is closed when application exits
        Runtime.getRuntime().addShutdownHook(new Thread(MongoConnection::closeConnection));
    }
}