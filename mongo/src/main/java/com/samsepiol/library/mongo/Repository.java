package com.samsepiol.library.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.samsepiol.library.core.util.DateTimeUtils;
import com.samsepiol.library.core.util.SerializationUtil;
import com.samsepiol.library.mongo.constants.EntityConstants;
import com.samsepiol.library.mongo.models.Entity;
import lombok.NonNull;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public interface Repository {
    ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(Boolean.TRUE);
    BulkWriteOptions BULK_WRITE_OPTIONS = new BulkWriteOptions().ordered(Boolean.FALSE);
    UpdateOptions UPSERT_OPTIONS = new UpdateOptions().upsert(Boolean.TRUE);

    default <T extends Entity> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        return getMongoTemplate().getDb()
                .withCodecRegistry(getCodecRegistry())
                .getCollection(name, clazz);
    }

    default MongoCollection<Document> getCollection(String name) {
        return getMongoTemplate().getDb()
                .withCodecRegistry(getCodecRegistry())
                .getCollection(name);
    }

    default <T extends Entity> T findById(String collectionName, String value, Class<T> clazz) {
        return getCollection(collectionName, clazz)
                .find(Filters.eq(EntityConstants.ID, value))
                .first();
    }

    default <T extends Entity> T findOne(String collectionName, String id, String value, Class<T> clazz) {
        return getCollection(collectionName, clazz)
                .find(Filters.eq(id, value))
                .first();
    }

    default <T extends Entity> List<T> findAll(String collectionName, String id, String value, Class<T> clazz) {
        return getCollection(collectionName, clazz)
                .find(Filters.eq(id, value))
                .into(new ArrayList<>());
    }

    default <T extends Entity> List<T> findAll(String collectionName, Bson query, Class<T> clazz) {
        return getCollection(collectionName, clazz)
                .find(query)
                .into(new ArrayList<>());
    }

    default <T extends Entity> T insert(String collectionName, T entity, Class<T> clazz) {
        entity.beforeInsert();
        getCollection(collectionName, clazz).insertOne(entity);
        return entity;
    }

    default <T extends Entity> List<T> insert(String collectionName, List<T> entities, Class<T> clazz) {
        if (entities.isEmpty()) {
            return entities;
        }
        entities.forEach(Entity::beforeInsert);
        getCollection(collectionName, clazz).insertMany(entities);
        return entities;
    }

    default boolean updateById(String collectionName, String value, Bson updateQuery) {
        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName)
                .updateOne(Filters.eq(EntityConstants.ID, value), query);
        return updateResult.getMatchedCount() > 0;
    }

    default boolean update(String collectionName, String id, String value, Bson updateQuery) {

        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName).updateMany(Filters.eq(id, value), query);
        return updateResult.getMatchedCount() > 0;
    }

    default <T extends Entity> boolean update(String collectionName, T entity) {
        entity.beforeUpdate();
        var query = new Document(SerializationUtil.convertToMap(entity));
        var updateResult = getCollection(collectionName).replaceOne(Filters.eq(EntityConstants.ID, entity.getId()), query);
        return updateResult.getMatchedCount() > 0;
    }

    default boolean upsertById(String collectionName, String value, Bson updateQuery) {
        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName)
                .updateOne(Filters.eq(EntityConstants.ID, value), query, UPSERT_OPTIONS);
        return updateResult.getMatchedCount() > 0;
    }

    default <T extends Entity> T upsert(String collectionName, T entity) {
        entity.beforeInsertOrUpdate();
        var query = new Document(SerializationUtil.convertToMap(entity));
        getCollection(collectionName).replaceOne(Filters.eq(EntityConstants.ID, entity.getId()), query, REPLACE_OPTIONS);
        return entity;
    }

    default <T extends Entity> List<T> upsert(String collectionName, List<T> entities) {
        if (entities.isEmpty()) {
            return entities;
        }

        var query = prepareBulkUpsertQuery(entities);
        getCollection(collectionName).bulkWrite(query, BULK_WRITE_OPTIONS);
        return entities;
    }

    private static <T extends Entity> List<ReplaceOneModel<Document>> prepareBulkUpsertQuery(List<T> entities) {
        return entities.stream()
                .map(entity -> {
                    entity.beforeInsertOrUpdate();
                    var update = new Document(SerializationUtil.convertToMap(entity));
                    return new ReplaceOneModel<>(
                            Filters.eq(EntityConstants.ID, entity.getId()),
                            update,
                            REPLACE_OPTIONS
                    );
                })
                .toList();
    }

    @NonNull
    MongoTemplate getMongoTemplate();

    @NonNull
    CodecRegistry getCodecRegistry();

    boolean isHealthy();
}
