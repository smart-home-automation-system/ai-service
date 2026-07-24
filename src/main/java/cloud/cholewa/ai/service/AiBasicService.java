package cloud.cholewa.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AiBasicService {

    private final ChatClient chatClient;

    public AiBasicService(final ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Mono<ResponseEntity<Object>> sendMessage(final String message) {
        return Mono.just(ResponseEntity.ok(chatClient.prompt().user(message).call().content()));
    }
}
