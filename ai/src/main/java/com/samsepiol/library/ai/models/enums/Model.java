package com.samsepiol.library.ai.models.enums;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Model {
    GEMINI("googleGenAiChatModel");

    private final List<String> beanNames;

    Model(@NonNull String... beanName) {
        this(List.of(beanName));
    }

    @NonNull
    public static Optional<Model> beanNameMatch(@NonNull String beanName) {
        return Arrays.stream(Model.values())
                .filter(model -> model.getBeanNames().contains(beanName))
                .findFirst();
    }
}
