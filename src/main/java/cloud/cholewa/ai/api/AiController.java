package cloud.cholewa.ai.api;

import cloud.cholewa.ai.service.AiBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiBasicService service;

    @PostMapping
    Mono<ResponseEntity<String>> sendMessage(@RequestBody final String ask) {
        return service.sendMessage(ask).map(ResponseEntity::ok);
    }
}
