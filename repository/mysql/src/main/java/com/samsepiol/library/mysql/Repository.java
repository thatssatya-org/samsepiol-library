package com.samsepiol.library.mysql;

import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DatabaseObject;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.Session;
import com.samsepiol.library.core.exception.LibraryRuntimeException;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.core.util.SerializationUtil;
import com.samsepiol.library.repository.models.Entity;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface Repository {

    @NonNull
    Session getSession();

    @NonNull
    String getSchemaName();

    default Collection getCollection(String name) {
        var schema = getSession().getSchema(getSchemaName());
        var collection = schema.getCollection(name);

        if (collection.existsInDatabase() == DatabaseObject.DbObjectStatus.NOT_EXISTS) {
            try {
                return schema.createCollection(name);
            } catch (XProtocolError error) {
                if (error.getErrorCode() == 1050) {
                    return getCollection(name);
                }
                throw error;
            }
        }

        return collection;
    }

    default <T extends Entity> T findById(@NonNull String collectionName, @NonNull String id, @NonNull Class<T> clazz) {
        var result = getCollection(collectionName)
                .find("_id = :id")
                .bind("id", id)
                .execute();

        return mapToEntity(result.fetchOne(), clazz);
    }

    default <T extends Entity> T findOne(@NonNull String collectionName, @NonNull String criteria, @NonNull Map<String, Object> params, @NonNull Class<T> clazz) {
        var find = getCollection(collectionName).find(criteria);
        params.forEach(find::bind);
        return mapToEntity(find.execute().fetchOne(), clazz);
    }

    default <T extends Entity> List<T> findAll(@NonNull String collectionName, @NonNull String criteria, @NonNull Map<String, Object> params, @NonNull Class<T> clazz) {
        var find = getCollection(collectionName).find(criteria);
        params.forEach(find::bind);

        return find.execute().fetchAll().stream()
                .map(doc -> mapToEntity(doc, clazz))
                .toList();
    }

    default <T extends Entity> T insert(String collectionName, T entity) {
        entity.beforeInsert();
        var result = getCollection(collectionName).add(mapToString(entity)).execute();
        entity.setId(result.getGeneratedIds().getFirst());
        return entity;
    }

    default <T extends Entity> List<T> insert(String collectionName, List<T> entities) {
        if (entities.isEmpty()) {
            return entities;
        }
        entities.forEach(Entity::beforeInsert);

        String[] jsonList = new String[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            jsonList[i] = mapToString(entities.get(i));
        }
        getCollection(collectionName).add(jsonList).execute();
        return entities;
    }

    private <T> T mapToEntity(DbDoc doc, Class<T> clazz) {
        try {
            return SerializationUtil.convertToEntity(doc.toString(), clazz);
        } catch (SerializationException e) {
            throw LibraryRuntimeException.wrap(e);
        }
    }

    private String mapToString(Entity entity) {
        try {
            return SerializationUtil.convertToString(entity);
        } catch (SerializationException e) {
            throw LibraryRuntimeException.wrap(e);
        }
    }

}
