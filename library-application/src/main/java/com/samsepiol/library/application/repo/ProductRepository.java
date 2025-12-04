package com.samsepiol.library.application.repo;

import com.samsepiol.library.application.models.Product;
import lombok.NonNull;

public interface ProductRepository {
    Product getById(@NonNull String id);

    Product insert(Product product);

    Product update(@NonNull String id, @NonNull String name);
}
