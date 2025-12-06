package com.samsepiol.library.ai;

import com.samsepiol.library.ai.annotation.AIEnabled;
import com.samsepiol.library.ai.models.enums.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@AIEnabled
@RequiredArgsConstructor
public class ChatController {
    private final Map<Model, ChatClient> chatClients;

    @GetMapping
    public ResponseEntity<ChatResponse> chat(@RequestParam Model model,
                                             @RequestParam String system,
                                             @RequestParam String userText) {

        var response = chatClients.get(model).prompt()
                .system(system)
                .user(userText)
                .call();
        return ResponseEntity.ok(response.chatResponse());
    }

}
