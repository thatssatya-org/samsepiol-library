package com.samsepiol.library.application.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.library.repository.models.Entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Value
@Jacksonized
@SuperBuilder
@AllArgsConstructor(onConstructor_ = {@BsonCreator})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Product extends Entity {
    private static final String PREFIX = "P";

    @BsonProperty("name")
    String name;

    @Override
    protected @NonNull String getIdPrefix() {
        return PREFIX;
    }
}
