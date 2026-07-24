package cloud.cholewa.ai.api;

import cloud.cholewa.ai.service.AiBasicService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiControllerTest {

    @Test
    void sendMessage_wrapsServiceReplyInOkResponse() {
        final AiBasicService service = mock(AiBasicService.class);
        when(service.sendMessage(anyString())).thenReturn(Mono.just("pong"));

        final AiController controller = new AiController(service);

        StepVerifier.create(controller.sendMessage("ping"))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isEqualTo("pong");
                })
                .verifyComplete();
    }
}
