package com.samsepiol.library.mongo.models;

import com.samsepiol.library.core.util.DateTimeUtils;
import com.samsepiol.library.core.util.IdentityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder(builderMethodName = "parentBuilder")
@ToString
public abstract class Entity {
    @BsonId
    private String id;
    private Long createdAt;
    private Long updatedAt;

    public void beforeInsert() {
        if (Objects.isNull(createdAt)) {
            createdAt = updatedAt = DateTimeUtils.currentEpochMillis();
        }

        if (Objects.isNull(id)) {
            id = IdentityUtils.generateId(getIdPrefix());
        }
    }

    public void beforeUpdate() {
        updatedAt = DateTimeUtils.currentEpochMillis();
    }

    @NonNull
    protected abstract String getIdPrefix();

}
