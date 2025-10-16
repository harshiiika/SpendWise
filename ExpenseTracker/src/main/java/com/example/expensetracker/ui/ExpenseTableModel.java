package com.example.expensetracker.ui;

import com.example.expensetracker.model.Expense;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpenseTableModel - Custom TableModel for displaying Expense objects in a JTable.
 * Handles formatting of data such as date and amount, and notifies JTable when data changes.
 */
public class ExpenseTableModel extends AbstractTableModel {

    // Column headers for the JTable
    private final String[] columns = {"Date", "Category", "Description", "Amount"};

    // Formatter for displaying dates
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // List to hold Expense objects
    private List<Expense> items = new ArrayList<>();

    /**
     * Sets the list of expenses to be displayed in the table.
     * Automatically refreshes the table view.
     *
     * @param items List of Expense objects
     */
    public void setItems(List<Expense> items) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = new ArrayList<>();
        }
        fireTableDataChanged(); // Notify JTable that data has changed
    }

    /**
     * Returns the Expense object at a specific row.
     *
     * @param row Row index
     * @return Expense at the given row
     */
    public Expense getExpenseAt(int row) {
        if (row >= 0 && row < items.size()) {
            return items.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < columns.length) {
            return columns[column];
        }
        return "";
    }

    /**
     * Returns the value for a specific cell in the table.
     *
     * @param rowIndex    Row index
     * @param columnIndex Column index
     * @return Formatted value for the cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= items.size()) return "";

        Expense expense = items.get(rowIndex);
        switch (columnIndex) {
            case 0: // Date column
                return expense.getDate() != null ? dateFormat.format(expense.getDate()) : "";
            case 1: // Category column
                return expense.getCategory();
            case 2: // Description column
                return expense.getDescription();
            case 3: // Amount column formatted to 2 decimal places
                return String.format("%.2f", expense.getAmount());
            default:
                return "";
        }
    }

    /**
     * Returns the class type for each column, useful for JTable renderers/editors.
     *
     * @param columnIndex Column index
     * @return Class of the column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return String.class; // Date as formatted string
            case 1: return String.class; // Category
            case 2: return String.class; // Description
            case 3: return String.class; // Amount (formatted string)
            default: return Object.class;
        }
    }

    /**
     * Indicates that cells are not editable in this table model.
     *
     * @param rowIndex    Row index
     * @param columnIndex Column index
     * @return false (all cells are read-only)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
