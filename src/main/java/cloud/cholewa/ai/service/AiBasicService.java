package cloud.cholewa.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
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
                .collect(Collectors.joining())
                .onErrorMap(this::sanitizeProviderFailure);
    }

    //OpenAI quotes the rejected credential back in its error message ("Incorrect API key provided: ..."),
    //and the default processor passes an unhandled message straight into the response body - which the
    //structured cluster logs now also persist in Loki. The exception type says what went wrong;
    //the provider's text is not worth storing.
    private Throwable sanitizeProviderFailure(final Throwable throwable) {
        log.error("AI provider call failed: {}", throwable.getClass().getSimpleName());

        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI provider call failed");
    }
}
