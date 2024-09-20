package cloud.cholewa.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AiBasicService {

    private final AiAssistant aiAssistant;

    public Mono<ResponseEntity<Object>> sendMessage(final String message) {
        return Mono.just(ResponseEntity.ok(aiAssistant.chat(message)));
    }
}
