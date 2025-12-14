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
                    client = MongoClients.create("mongodb://localhost:27017");
                }
                database = client.getDatabase("course_management_system");
            } catch (Exception e) {
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return database;
    }

}
