
package fr.yeensight.tarpinfur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MongoEnvDebug implements CommandLineRunner {

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
    public void run(String... args) {
        System.out.println("=== Mongo env resolved by Spring ===");
        System.out.println("host = " + host);
        System.out.println("port = " + port);
        System.out.println("database = " + database);
        System.out.println("username = " + username);
        System.out.println("password = " + (password == null || password.isBlank() ? "<empty>" : "<set>"));
        System.out.println("authentication-database = " + authDatabase);
    }
}