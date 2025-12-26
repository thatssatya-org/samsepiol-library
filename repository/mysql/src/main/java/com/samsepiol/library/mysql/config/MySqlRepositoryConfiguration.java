package com.samsepiol.library.mysql.config;

import com.mysql.cj.xdevapi.Client;
import com.mysql.cj.xdevapi.ClientFactory;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Configuration
@ConfigurationProperties(prefix = "mysql-config")
@ConditionalOnProperty("mysql-config.host")
public class MySqlRepositoryConfiguration {
    private String userName;
    private String password;
    private String host;
    private String database;

    private static final ClientFactory CLIENT_FACTORY = new ClientFactory();

    @Bean
    public Client xDevApiClient() {
        // Connection String Format: mysqlx://user:password@host:33060/
        String connectionString = String.format("mysqlx://%s:%s@%s:33060", userName, URLEncoder.encode(password, StandardCharsets.UTF_8), host);

        // This creates a thread-safe connection pool
        return CLIENT_FACTORY.getClient(connectionString, "{\"pooling\":{\"enabled\":true, \"maxSize\":10}}");
    }
}
