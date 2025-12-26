package com.samsepiol.library.mysql.impl;

import com.mysql.cj.xdevapi.Client;
import com.mysql.cj.xdevapi.Session;
import com.samsepiol.library.mysql.Repository;
import com.samsepiol.library.mysql.config.MySqlRepositoryConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@ConditionalOnProperty("mysql-config.host")
public class MySqlRepository implements Repository {
    private final Client client;
    private final MySqlRepositoryConfiguration configuration;

    @Override
    public @NonNull Session getSession() {
        return client.getSession();
    }

    @Override
    public @NonNull String getSchemaName() {
        return configuration.getDatabase();
    }
}
