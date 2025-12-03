package com.samsepiol.library.core.util;

import com.samsepiol.library.core.constants.Strings;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class IdentityUtils {
    private static final String ID_FORMAT_1 = "%s%s";

    @NonNull
    public static String generateId(String prefix) {
        return String.format(ID_FORMAT_1, prefix, generateId());
    }

    @NonNull
    public static String generateId() {
        return UUID.randomUUID().toString().replace(Strings.HYPHEN, Strings.EMPTY);
    }
}
