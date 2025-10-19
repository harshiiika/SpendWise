package com.example.expensetracker.model;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Date;

public class Expense {
    private ObjectId id;
    private double amount;
    private String category;
    private String description;
    private Date date;

    public Expense() {}

    public Expense(double amount, String category, String description, Date date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    // Convert to BSON Document (for MongoDB)
    public Document toDocument() {
        Document doc = new Document("amount", amount)
                .append("category", category)
                .append("description", description)
                .append("date", date);
        if (id != null) doc.append("_id", id);
        return doc;
    }

    // Convert from BSON Document to Expense object
    public static Expense fromDocument(Document doc) {
        Expense e = new Expense();
        if (doc.containsKey("_id")) e.setId(doc.getObjectId("_id"));
        e.setAmount(doc.getDouble("amount"));
        e.setCategory(doc.getString("category"));
        e.setDescription(doc.getString("description"));
        e.setDate(doc.getDate("date"));
        return e;
    }
}
