package pe.edu.vallegrande.agedetector.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.agedetector.dto.ChatGptRequest;
import pe.edu.vallegrande.agedetector.dto.ChatGptResponse;
import pe.edu.vallegrande.agedetector.model.entity.Conversation;
import pe.edu.vallegrande.agedetector.model.entity.Message;
import pe.edu.vallegrande.agedetector.model.entity.MessageType;
import pe.edu.vallegrande.agedetector.repository.ConversationRepository;
import pe.edu.vallegrande.agedetector.repository.MessageRepository;
import pe.edu.vallegrande.agedetector.service.ChatGptService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class ChatGptServiceImpl implements ChatGptService {

        private final WebClient webClient;
        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final String apiKey;
        private final String chatgptUrl;
        private final String chatgptHost;

        public ChatGptServiceImpl(WebClient.Builder webClientBuilder,
                        ConversationRepository conversationRepository,
                        MessageRepository messageRepository,
                        @Value("${external-apis.rapidapi.key}") String apiKey,
                        @Value("${external-apis.rapidapi.chatgpt.url}") String chatgptUrl,
                        @Value("${external-apis.rapidapi.chatgpt.host}") String chatgptHost) {
                this.webClient = webClientBuilder.build();
                this.conversationRepository = conversationRepository;
                this.messageRepository = messageRepository;
                this.apiKey = apiKey;
                this.chatgptUrl = chatgptUrl;
                this.chatgptHost = chatgptHost;
        }

        @Override
        public Mono<ChatGptResponse> sendMessage(String conversationId, String message) {
                Mono<Conversation> conversationMono = (conversationId != null && !conversationId.isEmpty())
                                ? conversationRepository.findById(conversationId)
                                                .switchIfEmpty(conversationRepository.save(Conversation.builder()
                                                                .createdAt(LocalDateTime.now()).build()))
                                : conversationRepository
                                                .save(Conversation.builder().createdAt(LocalDateTime.now()).build());

                return conversationMono.flatMap(conversation -> {
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
                                                boolean isSuccessful = response.isSuccess() ||
                                                                (response.getResult() != null
                                                                                && !response.getResult().isEmpty())
                                                                ||
                                                                (response.getContent() != null
                                                                                && !response.getContent().isEmpty());

                                                Message newMessage = Message.builder()
                                                                .conversationId(conversation.getId())
                                                                .type(MessageType.TEXT)
                                                                .timestamp(LocalDateTime.now())
                                                                .success(isSuccessful)
                                                                .errorMessage(response.getError())
                                                                .question(message)
                                                                .response(response.getResult() != null
                                                                                ? response.getResult()
                                                                                : response.getContent())
                                                                .webAccess(request.isWeb_access())
                                                                .build();

                                                response.setSuccess(isSuccessful);

                                                return messageRepository.save(newMessage)
                                                                .thenReturn(response);
                                        })
                                        .onErrorResume(throwable -> {
                                                Message errorMessage = Message.builder()
                                                                .conversationId(conversation.getId())
                                                                .type(MessageType.TEXT)
                                                                .timestamp(LocalDateTime.now())
                                                                .success(false)
                                                                .errorMessage("Error al comunicarse con ChatGPT: "
                                                                                + throwable.getMessage())
                                                                .question(message)
                                                                .build();

                                                ChatGptResponse errorResponse = ChatGptResponse.builder()
                                                                .success(false)
                                                                .error("Error al comunicarse con ChatGPT: "
                                                                                + throwable.getMessage())
                                                                .build();

                                                return messageRepository.save(errorMessage)
                                                                .thenReturn(errorResponse);
                                        });
                });
        }

        @Override
        public Mono<ChatGptResponse> sendMessageOnly(String message) {
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
                                .doOnSuccess(response -> {
                                        boolean isSuccessful = response.isSuccess() ||
                                                        (response.getResult() != null
                                                                        && !response.getResult().isEmpty())
                                                        ||
                                                        (response.getContent() != null
                                                                        && !response.getContent().isEmpty());
                                        response.setSuccess(isSuccessful);
                                })
                                .onErrorResume(throwable -> {
                                        ChatGptResponse errorResponse = ChatGptResponse.builder()
                                                        .success(false)
                                                        .error("Error al comunicarse con ChatGPT: "
                                                                        + throwable.getMessage())
                                                        .build();
                                        return Mono.just(errorResponse);
                                });
        }
}
