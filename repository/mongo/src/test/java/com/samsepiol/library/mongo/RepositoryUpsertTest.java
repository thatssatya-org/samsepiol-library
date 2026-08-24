package com.samsepiol.library.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.samsepiol.library.mongo.impl.DefaultRepository;
import com.samsepiol.library.repository.models.Entity;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryUpsertTest {
    private static final String COLLECTION = "entities";

    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private CodecRegistry codecRegistry;
    @Mock
    private MongoDatabase database;
    @Mock
    private MongoCollection<Document> collection;
    @InjectMocks
    private DefaultRepository repository;

    @Test
    void upsertSetsImmutableFieldsOnlyWhenInserting() {
        when(mongoTemplate.getDb()).thenReturn(database);
        when(database.withCodecRegistry(codecRegistry)).thenReturn(database);
        when(database.getCollection(COLLECTION)).thenReturn(collection);
        var entity = new TestEntity();

        repository.upsert(COLLECTION, entity, new Document("naturalKey", "value"));

        var updateCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection).updateOne(any(Bson.class), updateCaptor.capture(), eq(Repository.UPSERT_OPTIONS));

        var update = updateCaptor.getValue().toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry());
        assertFalse(update.getDocument("$set").containsKey("_id"));
        assertFalse(update.getDocument("$set").containsKey("createdAt"));
        assertEquals(entity.getId(), update.getDocument("$setOnInsert").getString("_id").getValue());
        assertEquals(entity.getCreatedAt(), update.getDocument("$setOnInsert").getInt64("createdAt").getValue());
        assertTrue(update.getDocument("$set").containsKey("updatedAt"));
    }

    private static final class TestEntity extends Entity {
        @Override
        protected String getIdPrefix() {
            return "TE";
        }
    }
}
