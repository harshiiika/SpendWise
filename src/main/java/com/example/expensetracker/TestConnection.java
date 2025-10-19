package com.example.expensetracker;

import com.example.expensetracker.db.MongoConnection;
import com.mongodb.client.MongoDatabase;

public class TestConnection {
    public static void main(String[] args) {
        try {
            MongoDatabase db = MongoConnection.getDatabase();
            System.out.println("✅ Connected to database: " + db.getName());
        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoConnection.closeConnection();
        }
    }
}
