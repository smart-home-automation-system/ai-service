package cloud.cholewa.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class AiBasicService {

    private final ChatClient chatClient;

    public AiBasicService(final ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Mono<String> sendMessage(final String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .collect(Collectors.joining());
    }
}
