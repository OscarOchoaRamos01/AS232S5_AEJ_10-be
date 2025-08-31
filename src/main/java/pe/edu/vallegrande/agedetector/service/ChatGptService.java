package pe.edu.vallegrande.agedetector.service;

import pe.edu.vallegrande.agedetector.model.ChatGptResponse;
import reactor.core.publisher.Mono;

public interface ChatGptService {

    Mono<ChatGptResponse> sendMessage(String message);
}
