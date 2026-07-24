package cloud.cholewa.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiBasicServiceTest {

    @Test
    void sendMessage_aggregatesStreamedContent() {
        final ChatClient.Builder builder = mock(ChatClient.Builder.class);
        final ChatClient chatClient = mock(ChatClient.class);
        final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        final ChatClient.StreamResponseSpec streamSpec = mock(ChatClient.StreamResponseSpec.class);

        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.stream()).thenReturn(streamSpec);
        when(streamSpec.content()).thenReturn(Flux.just("po", "ng"));

        final AiBasicService service = new AiBasicService(builder);

        service.sendMessage("ping")
            .as(StepVerifier::create)
            .expectNext("pong")
            .verifyComplete();
    }
}
