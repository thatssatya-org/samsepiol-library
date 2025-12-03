package com.samsepiol.library.application.mongo.models;

import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Product extends Entity {
    private static final String PREFIX = "P";

    String name;

    @Override
    protected @NonNull String getIdPrefix() {
        return PREFIX;
    }
}
