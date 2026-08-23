package com.samsepiol.library.token.management.persistence;

import com.samsepiol.library.mongo.codec.CodecSupplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenManagementCodecSupplier implements CodecSupplier {
    private static final List<Class<?>> CLASSES = List.of(TokenRecordEntity.class);

    @Override
    public @NonNull List<Class<?>> getManagedClasses() {
        return CLASSES;
    }
}
