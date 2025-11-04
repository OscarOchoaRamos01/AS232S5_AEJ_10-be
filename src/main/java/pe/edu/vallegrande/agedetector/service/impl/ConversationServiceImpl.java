package pe.edu.vallegrande.agedetector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.agedetector.dto.*;
import pe.edu.vallegrande.agedetector.model.entity.Conversation;
import pe.edu.vallegrande.agedetector.model.entity.Message;
import pe.edu.vallegrande.agedetector.model.entity.MessageType;
import pe.edu.vallegrande.agedetector.repository.ConversationRepository;
import pe.edu.vallegrande.agedetector.repository.MessageRepository;
import pe.edu.vallegrande.agedetector.service.ConversationService;
import pe.edu.vallegrande.agedetector.service.ChatGptService;
import pe.edu.vallegrande.agedetector.service.ImageGeneratorService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final ChatGptService chatGptService;
        private final ImageGeneratorService imageGeneratorService;

        @Override
        public Mono<ConversationResponse> createConversation(ConversationRequest request) {
                log.info("Creando nueva conversación con tipo de servicio: {}", request.getServiceType());

                // Crear nueva conversación usando el contenido como título
                String title = request.getContent().length() > 50 ? request.getContent().substring(0, 50) + "..."
                                : request.getContent();

                Conversation newConversation = Conversation.builder()
                                .title(title)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .messageCount(0)
                                .lastMessageType(request.getServiceType())
                                .build();

                return conversationRepository.save(newConversation)
                                .flatMap(savedConversation -> {
                                        // Procesar el primer mensaje
                                        return processMessage(savedConversation.getId(), request.getContent(),
                                                        request.getServiceType())
                                                        .then(buildConversationResponse(savedConversation.getId()));
                                })
                                .doOnSuccess(response -> log.info("Conversación creada exitosamente con ID: {}",
                                                response.getConversationId()))
                                .doOnError(error -> log.error("Error creando conversación", error));
        }

        @Override
        public Mono<ConversationResponse> continueConversation(String conversationId, MessageRequest request) {
                log.info("Continuando conversación: {} con tipo de servicio: {}", conversationId,
                                request.getServiceType());

                return conversationRepository.findById(conversationId)
                                .switchIfEmpty(Mono.error(
                                                new RuntimeException("Conversación no encontrada: " + conversationId)))
                                .flatMap(conversation -> {
                                        // Procesar el nuevo mensaje
                                        return processMessage(conversationId, request.getContent(),
                                                        request.getServiceType())
                                                        .then(updateConversationMetadata(conversation,
                                                                        request.getServiceType()))
                                                        .then(buildConversationResponse(conversationId));
                                })
                                .doOnSuccess(response -> log.info("Conversación continuada exitosamente: {}",
                                                conversationId))
                                .doOnError(error -> log.error("Error continuando conversación: {}", conversationId,
                                                error));
        }

        private Mono<Void> processMessage(String conversationId, String content, String serviceType) {
                // Guardar mensaje de usuario
                MessageType requestType = serviceType.equals("CHATGPT") ? MessageType.CHATGPT_REQUEST
                                : MessageType.IMAGE_REQUEST;
                MessageType responseType = serviceType.equals("CHATGPT") ? MessageType.CHATGPT_RESPONSE
                                : MessageType.IMAGE_RESPONSE;

                Message userMessage = Message.builder()
                                .conversationId(conversationId)
                                .type(requestType)
                                .content(content)
                                .timestamp(LocalDateTime.now())
                                .build();

                return messageRepository.save(userMessage)
                                .then(processServiceResponse(conversationId, content, serviceType, responseType))
                                .then();
        }

        private Mono<Void> processServiceResponse(String conversationId, String content, String serviceType,
                        MessageType responseType) {
                if (serviceType.equals("CHATGPT")) {
                        return chatGptService.sendMessageOnly(content)
                                        .flatMap(response -> {
                                                Message responseMessage = Message.builder()
                                                                .conversationId(conversationId)
                                                                .type(responseType)
                                                                .content(response.getResult() != null
                                                                                ? response.getResult()
                                                                                : response.getContent())
                                                                .timestamp(LocalDateTime.now())
                                                                .success(response.isSuccess())
                                                                .errorMessage(response.getError())
                                                                .build();
                                                return messageRepository.save(responseMessage);
                                        })
                                        .then();
                } else if (serviceType.equals("IMAGE")) {
                        return imageGeneratorService.generateImageOnly(content)
                                        .flatMap(response -> {
                                                String contentText = response.isSuccess()
                                                                ? "Imagen generada: " + content
                                                                : "Error generando imagen: "
                                                                                + (response.getMessage() != null
                                                                                                ? response.getMessage()
                                                                                                : "Error desconocido");

                                                Message responseMessage = Message.builder()
                                                                .conversationId(conversationId)
                                                                .type(responseType)
                                                                .content(contentText)
                                                                .imageUrl(response.getImage_url())
                                                                .timestamp(LocalDateTime.now())
                                                                .success(response.isSuccess())
                                                                .build();
                                                return messageRepository.save(responseMessage);
                                        })
                                        .then();
                }

                return Mono.error(new IllegalArgumentException("Tipo de servicio no soportado: " + serviceType));
        }

        private Mono<Conversation> updateConversationMetadata(Conversation conversation, String serviceType) {
                return messageRepository.countByConversationId(conversation.getId())
                                .map(count -> {
                                        conversation.setUpdatedAt(LocalDateTime.now());
                                        conversation.setMessageCount(count.intValue());
                                        conversation.setLastMessageType(serviceType);
                                        return conversation;
                                })
                                .flatMap(conversationRepository::save);
        }

        private Mono<ConversationResponse> buildConversationResponse(String conversationId) {
                return conversationRepository.findById(conversationId)
                                .flatMap(conversation -> messageRepository
                                                .findByConversationIdOrderByTimestampAsc(conversationId)
                                                .map(this::mapToMessageResponse)
                                                .collectList()
                                                .map(messages -> ConversationResponse.builder()
                                                                .conversationId(conversation.getId())
                                                                .title(conversation.getTitle())
                                                                .createdAt(conversation.getCreatedAt())
                                                                .updatedAt(conversation.getUpdatedAt())
                                                                .messageCount(conversation.getMessageCount())
                                                                .lastMessageType(conversation.getLastMessageType())
                                                                .messages(messages)
                                                                .build()));
        }

        @Override
        public Mono<ConversationResponse> getConversationById(String conversationId) {
                return buildConversationResponse(conversationId);
        }

        @Override
        public Mono<Void> deleteConversation(String conversationId) {
                log.info("🗑️ Eliminando lógicamente conversación: {}", conversationId);

                return conversationRepository.findById(conversationId)
                                .switchIfEmpty(Mono.error(
                                                new RuntimeException("Conversación no encontrada: " + conversationId)))
                                .flatMap(conversation -> {
                                        conversation.setStatus("I");
                                        conversation.setUpdatedAt(LocalDateTime.now());
                                        return conversationRepository.save(conversation);
                                })
                                .then()
                                .doOnSuccess(unused -> log.info("✅ Conversación eliminada lógicamente: {}",
                                                conversationId))
                                .doOnError(error -> log.error("❌ Error eliminando conversación: {}", conversationId,
                                                error));
        }

        @Override
        public Mono<Void> restoreConversation(String conversationId) {
                log.info("🔄 Restaurando conversación: {}", conversationId);

                return conversationRepository.findById(conversationId)
                                .switchIfEmpty(Mono.error(
                                                new RuntimeException("Conversación no encontrada: " + conversationId)))
                                .flatMap(conversation -> {
                                        conversation.setStatus("A");
                                        conversation.setUpdatedAt(LocalDateTime.now());
                                        return conversationRepository.save(conversation);
                                })
                                .then()
                                .doOnSuccess(unused -> log.info("✅ Conversación restaurada: {}", conversationId))
                                .doOnError(error -> log.error("❌ Error restaurando conversación: {}", conversationId,
                                                error));
        }

        @Override
        public Flux<ConversationResponse> getActiveConversations() {
                log.info("📋 Obteniendo conversaciones activas (status = A)");

                return conversationRepository.findByStatusOrderByUpdatedAtDesc("A")
                                .flatMap(conversation -> buildConversationResponse(conversation.getId()));
        }

        @Override
        public Flux<ConversationResponse> getInactiveConversations() {
                log.info("🗑️ Obteniendo conversaciones inactivas (status = I)");

                return conversationRepository.findByStatusOrderByUpdatedAtDesc("I")
                                .flatMap(conversation -> buildConversationResponse(conversation.getId()));
        }

        private ConversationResponse mapToConversationResponse(Conversation conversation) {
                return ConversationResponse.builder()
                                .conversationId(conversation.getId())
                                .title(conversation.getTitle())
                                .createdAt(conversation.getCreatedAt())
                                .updatedAt(conversation.getUpdatedAt())
                                .messageCount(conversation.getMessageCount())
                                .lastMessageType(conversation.getLastMessageType())
                                .build();
        }

        private MessageResponse mapToMessageResponse(Message message) {
                return MessageResponse.builder()
                                .id(message.getId())
                                .type(message.getType())
                                .content(message.getContent())
                                .imageUrl(message.getImageUrl())
                                .timestamp(message.getTimestamp())
                                .build();
        }
}