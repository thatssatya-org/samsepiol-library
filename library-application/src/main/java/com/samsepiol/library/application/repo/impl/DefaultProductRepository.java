package com.samsepiol.library.application.repo.impl;

import com.mongodb.client.model.Updates;
import com.samsepiol.library.application.models.Product;
import com.samsepiol.library.application.repo.ProductRepository;
import com.samsepiol.library.mongo.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class DefaultProductRepository implements ProductRepository {
    private static final String COLLECTION_NAME = "products";
    private final Repository repository;

    @Override
    public Product getById(@NonNull String id) {
        return repository.findById(COLLECTION_NAME, id, Product.class);
    }

    @Override
    public Product insert(Product product) {
        return repository.insert(COLLECTION_NAME, product, Product.class);
    }

    @Override
    public Product update(@NonNull String id, @NonNull String name) {
        repository.updateById(COLLECTION_NAME, id, Updates.set("name", name));
        return getById(id);
    }
}
