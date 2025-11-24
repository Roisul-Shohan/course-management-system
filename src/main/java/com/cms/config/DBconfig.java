package com.cms.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBconfig {
    private static MongoClient client = null;
    private static MongoDatabase database = null;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                if (client == null) {
                    String uri = System.getenv("MONGODB_URI");
                    if (uri == null || uri.isEmpty()) {
                        uri = "mongodb://localhost:27017";
                    }
                    client = MongoClients.create(uri);
                }
                database = client.getDatabase("course_management_system");
            } catch (Exception e) {
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return database;
    }

}
