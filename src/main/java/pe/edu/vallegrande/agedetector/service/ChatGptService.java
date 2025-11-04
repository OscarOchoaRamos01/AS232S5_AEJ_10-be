package pe.edu.vallegrande.agedetector.service;

import pe.edu.vallegrande.agedetector.dto.ChatGptResponse;
import reactor.core.publisher.Mono;

public interface ChatGptService {

    Mono<ChatGptResponse> sendMessage(String conversationId, String message);

    Mono<ChatGptResponse> sendMessageOnly(String message);
}
