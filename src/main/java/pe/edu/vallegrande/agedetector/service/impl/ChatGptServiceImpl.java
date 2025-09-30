package pe.edu.vallegrande.agedetector.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.agedetector.model.ChatGptRequest;
import pe.edu.vallegrande.agedetector.model.ChatGptResponse;
import pe.edu.vallegrande.agedetector.model.entity.ChatConversation;
import pe.edu.vallegrande.agedetector.repository.ChatConversationRepository;
import pe.edu.vallegrande.agedetector.service.ChatGptService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class ChatGptServiceImpl implements ChatGptService {

        private final WebClient webClient;
        private final ChatConversationRepository chatConversationRepository;
        private final String apiKey;
        private final String chatgptUrl;
        private final String chatgptHost;

        public ChatGptServiceImpl(WebClient.Builder webClientBuilder,
                        ChatConversationRepository chatConversationRepository,
                        @Value("${external-apis.rapidapi.key}") String apiKey,
                        @Value("${external-apis.rapidapi.chatgpt.url}") String chatgptUrl,
                        @Value("${external-apis.rapidapi.chatgpt.host}") String chatgptHost) {
                this.webClient = webClientBuilder.build();
                this.chatConversationRepository = chatConversationRepository;
                this.apiKey = apiKey;
                this.chatgptUrl = chatgptUrl;
                this.chatgptHost = chatgptHost;
        }

        @Override
        public Mono<ChatGptResponse> sendMessage(String message) {
                ChatGptRequest request = ChatGptRequest.builder()
                                .messages(Collections.singletonList(
                                                ChatGptRequest.Message.builder()
                                                                .role("user")
                                                                .content(message)
                                                                .build()))
                                .web_access(false)
                                .build();

                return webClient.post()
                                .uri(chatgptUrl)
                                .header("X-RapidAPI-Key", apiKey)
                                .header("X-RapidAPI-Host", chatgptHost)
                                .header("Content-Type", "application/json")
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(ChatGptResponse.class)
                                .flatMap(response -> {
                                        // Determinar si la respuesta es exitosa
                                        boolean isSuccessful = response.isSuccess() ||
                                                        (response.getResult() != null
                                                                        && !response.getResult().isEmpty())
                                                        ||
                                                        (response.getContent() != null
                                                                        && !response.getContent().isEmpty());

                                        // Guardar la conversación en la base de datos
                                        ChatConversation conversation = ChatConversation.builder()
                                                        .question(message)
                                                        .response(response.getResult() != null ? response.getResult()
                                                                        : response.getContent())
                                                        .timestamp(LocalDateTime.now())
                                                        .webAccess(request.isWeb_access())
                                                        .success(isSuccessful)
                                                        .errorMessage(response.getError())
                                                        .build();

                                        // Actualizar el response para que también tenga success correcto
                                        response.setSuccess(isSuccessful);

                                        return chatConversationRepository.save(conversation)
                                                        .thenReturn(response);
                                })
                                .onErrorResume(throwable -> {
                                        // Guardar el error en la base de datos
                                        ChatConversation conversation = ChatConversation.builder()
                                                        .question(message)
                                                        .response(null)
                                                        .timestamp(LocalDateTime.now())
                                                        .webAccess(false)
                                                        .success(false)
                                                        .errorMessage("Error al comunicarse con ChatGPT: "
                                                                        + throwable.getMessage())
                                                        .build();

                                        ChatGptResponse errorResponse = ChatGptResponse.builder()
                                                        .success(false)
                                                        .error("Error al comunicarse con ChatGPT: "
                                                                        + throwable.getMessage())
                                                        .build();

                                        return chatConversationRepository.save(conversation)
                                                        .thenReturn(errorResponse);
                                });
        }
}
