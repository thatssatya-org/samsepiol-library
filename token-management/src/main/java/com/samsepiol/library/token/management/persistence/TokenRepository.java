package com.samsepiol.library.token.management.persistence;

import com.samsepiol.library.token.management.TokenReference;
import lombok.NonNull;

import java.util.Optional;

/** Persistence abstraction deliberately offers a single addressed read and no enumeration API. */
public interface TokenRepository {
    @NonNull
    Optional<TokenRecord> find(@NonNull TokenReference reference);

    @NonNull
    TokenRecord upsert(@NonNull TokenRecord record);
}
