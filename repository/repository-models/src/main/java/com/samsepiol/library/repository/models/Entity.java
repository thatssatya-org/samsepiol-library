package com.samsepiol.library.repository.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.samsepiol.library.core.util.DateTimeUtils;
import com.samsepiol.library.core.util.IdentityUtils;
import com.samsepiol.library.repository.constants.EntityConstants;
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
@SuperBuilder(builderMethodName = "parentBuilder", toBuilder = true)
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class Entity {
    @BsonId
    @JsonProperty(EntityConstants.ID)
    private String id;
    private Long createdAt;
    private Long updatedAt;

    public void beforeInsertOrUpdate() {
        if (Objects.isNull(id)) {
            beforeInsert();
        } else {
            beforeUpdate();
        }
    }

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
