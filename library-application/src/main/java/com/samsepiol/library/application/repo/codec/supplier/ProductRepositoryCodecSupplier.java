package com.samsepiol.library.application.repo.codec.supplier;

import com.samsepiol.library.application.models.Product;
import com.samsepiol.library.mongo.codec.CodecSupplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRepositoryCodecSupplier implements CodecSupplier {
    private static final List<Class<?>> CLASSES = List.of(Product.class);

    @Override
    public @NonNull List<Class<?>> getManagedClasses() {
        return CLASSES;
    }
}
