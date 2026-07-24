package cloud.cholewa.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiBasicServiceTest {

    @Test
    void sendMessage_wrapsModelReplyInOkResponse() {
        final ChatClient.Builder builder = mock(ChatClient.Builder.class);
        final ChatClient chatClient = mock(ChatClient.class);
        final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        final ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("pong");

        final AiBasicService service = new AiBasicService(builder);

        StepVerifier.create(service.sendMessage("ping"))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isEqualTo("pong");
                })
                .verifyComplete();
    }
}
