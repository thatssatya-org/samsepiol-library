package com.samsepiol.library.mongo.config;

import com.mongodb.MongoClientSettings;
import com.samsepiol.library.mongo.codec.CodecSupplier;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class RepositoryConfiguration {

    @Bean
    @Primary
    public CodecRegistry codecRegistry(List<CodecSupplier> codecSuppliers) {
        var allClasses = Objects.requireNonNullElse(codecSuppliers, Collections.<CodecSupplier>emptyList())
                .stream()
                .map(CodecSupplier::getManagedClasses)
                .flatMap(Collection::stream)
                .toList();

        var provider = PojoCodecProvider.builder()
                .register(allClasses.toArray(new Class<?>[0]))
                .build();

        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(provider));
    }

}
