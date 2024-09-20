package cloud.cholewa.ai.service;

import dev.langchain4j.service.spring.AiService;

@AiService
public interface AiAssistant {

    String chat(String message);
}
