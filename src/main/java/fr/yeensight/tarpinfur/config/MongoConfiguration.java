package fr.yeensight.tarpinfur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.ConnectionString;

@Configuration
@EnableMongoRepositories(basePackages = "fr.yeensight.tarpinfur.repository")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private String port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database:}")
    private String authDatabase;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        String connectionString;
        if (username != null && !username.isBlank()) {
            connectionString = String.format(
                    "mongodb://%s:%s@%s:%s/%s?authSource=%s",
                    username, password, host, port, database, authDatabase
            );
        } else {
            connectionString = String.format(
                    "mongodb://%s:%s/%s",
                    host, port, database
            );
        }
        return MongoClients.create(new ConnectionString(connectionString));
    }
}