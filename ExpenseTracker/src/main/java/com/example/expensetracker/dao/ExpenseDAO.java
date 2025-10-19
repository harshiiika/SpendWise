package com.example.expensetracker.dao;

import com.example.expensetracker.db.MongoConnection;
import com.example.expensetracker.model.Expense;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    private final MongoCollection<Document> collection = MongoConnection.getDatabase().getCollection("expenses");

    // Insert new expense
    public void insertExpense(Expense expense) {
        Document doc = expense.toDocument();
        collection.insertOne(doc);
        if (doc.containsKey("_id")) {
            expense.setId(doc.getObjectId("_id"));
        }
    }

    // Fetch all expenses
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(new Document("date", -1));
        for (Document doc : docs) {
            expenses.add(Expense.fromDocument(doc));
        }
        return expenses;
    }
}
