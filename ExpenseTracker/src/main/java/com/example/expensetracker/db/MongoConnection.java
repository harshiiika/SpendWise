package com.example.expensetracker.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {


    private static final String CONNECTION_STRING = "mongodb+srv://harshikasaxena01:Golgappe@expensecluster.vykbkqd.mongodb.net/?retryWrites=true&w=majority&appName=ExpenseCluster";
    private static final String DATABASE_NAME = "expenseTracker";

    private static MongoClient mongoClient = null;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
