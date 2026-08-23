package com.samsepiol.library.token.management.persistence;

import com.samsepiol.library.token.management.TokenReference;
import lombok.NonNull;

import jakarta.annotation.Nullable;

/** Persistence abstraction deliberately offers a single addressed read and no enumeration API. */
public interface TokenRepository {
    @Nullable
    TokenRecordEntity find(@NonNull TokenReference reference);

    @NonNull
    TokenRecordEntity upsert(@NonNull TokenRecordEntity record);
}
