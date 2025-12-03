package com.samsepiol.library.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.samsepiol.library.core.util.DateTimeUtils;
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

    default boolean updateById(String collectionName, String value, Bson updateQuery) {
        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName)
                .updateOne(Filters.eq(EntityConstants.ID, value), query);
        return updateResult.getModifiedCount() > 0;
    }

    default boolean update(String collectionName, String id, String value, Bson updateQuery) {

        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName).updateMany(Filters.eq(id, value), query);
        return updateResult.getModifiedCount() > 0;
    }

    default boolean upsertById(String collectionName, String value, Bson updateQuery) {
        var query = Updates.combine(updateQuery, Updates.set(EntityConstants.UPDATED_AT, DateTimeUtils.currentEpochMillis()));
        var updateResult = getCollection(collectionName)
                .updateOne(Filters.eq(EntityConstants.ID, value), query, new UpdateOptions().upsert(Boolean.TRUE));
        return updateResult.getModifiedCount() > 0;
    }

    @NonNull
    MongoTemplate getMongoTemplate();

    @NonNull
    CodecRegistry getCodecRegistry();
}
