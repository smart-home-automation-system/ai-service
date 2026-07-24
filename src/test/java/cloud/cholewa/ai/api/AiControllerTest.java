package cloud.cholewa.ai.api;

import cloud.cholewa.ai.service.AiBasicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(AiController.class)
class AiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AiBasicService aiBasicService;

    @Test
    void should_successfully_send_message() {
        when(aiBasicService.sendMessage(anyString())).thenReturn(Mono.just("pong"));

        webTestClient.post()
            .uri(UriBuilder::build)
            .body(BodyInserters.fromValue("ping"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("pong");
    }

    @Test
    void should_fail_when_body_is_missing() {
        webTestClient.post()
            .uri(UriBuilder::build)
            .contentType(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
