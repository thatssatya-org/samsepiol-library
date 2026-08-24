package com.samsepiol.library.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.samsepiol.library.repository.models.Entity;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RepositoryUpsertTest {
    private static final String COLLECTION = "entities";

    @Test
    void upsertSetsIdOnlyWhenInserting() {
        var collection = mock(MongoCollection.class);
        var repository = new TestRepository(collection);
        var entity = new TestEntity();
        entity.setId("TE-existing-id");

        repository.upsert(COLLECTION, entity, new Document("naturalKey", "value"));

        var updateCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection).updateOne(any(Bson.class), updateCaptor.capture(), eq(Repository.UPSERT_OPTIONS));

        var update = updateCaptor.getValue().toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry());
        assertFalse(update.getDocument("$set").containsKey("_id"));
        assertEquals("TE-existing-id", update.getDocument("$setOnInsert").getString("_id").getValue());
        assertTrue(update.getDocument("$set").containsKey("updatedAt"));
    }

    private static final class TestRepository implements Repository {
        private final MongoCollection<Document> collection;

        private TestRepository(MongoCollection<Document> collection) {
            this.collection = collection;
        }

        @Override
        public MongoCollection<Document> getCollection(String name) {
            return collection;
        }

        @Override
        public MongoTemplate getMongoTemplate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CodecRegistry getCodecRegistry() {
            return MongoClientSettings.getDefaultCodecRegistry();
        }

        @Override
        public boolean isHealthy() {
            return true;
        }
    }

    private static final class TestEntity extends Entity {
        @Override
        protected String getIdPrefix() {
            return "TE";
        }
    }
}
