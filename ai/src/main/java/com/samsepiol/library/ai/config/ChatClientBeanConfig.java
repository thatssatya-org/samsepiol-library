package com.samsepiol.library.ai.config;

import com.samsepiol.library.ai.annotation.AIEnabled;
import com.samsepiol.library.ai.models.enums.Model;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@AIEnabled
public class ChatClientBeanConfig {

    @Bean
    @AIEnabled
    public Map<Model, ChatClient> chatClients(Map<String, ChatModel> chatModels) {
        return chatModels.entrySet().stream()
                .map(entry -> Model.beanNameMatch(entry.getKey())
                        .map(model -> Map.entry(model, ChatClient.builder(entry.getValue()).build())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
