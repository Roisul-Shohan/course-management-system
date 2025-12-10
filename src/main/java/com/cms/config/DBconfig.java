package com.cms.config;

import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
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

                          // Configure MongoDB client with timeouts and connection pooling
                          // Increase connection timeouts to be more tolerant in cloud environments.
                          MongoClientSettings settings = MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(uri))
                            .applyToConnectionPoolSettings(builder ->
                                builder.maxSize(10) // Maximum pool size
                                       .minSize(2)   // Minimum pool size
                                    .maxWaitTime(10000, TimeUnit.MILLISECONDS) // Max wait time for connection
                            )
                            .applyToSocketSettings(builder ->
                                builder.connectTimeout(10000, TimeUnit.MILLISECONDS) // Connection timeout (increased from 3s)
                                    .readTimeout(20000, TimeUnit.MILLISECONDS)   // Read timeout (increased)
                            )
                            .applyToClusterSettings(builder ->
                                builder.serverSelectionTimeout(30000, TimeUnit.MILLISECONDS) // Server selection timeout (increased)
                            )
                            .build();

                    client = MongoClients.create(settings);
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
