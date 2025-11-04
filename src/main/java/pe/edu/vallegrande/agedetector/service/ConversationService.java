package pe.edu.vallegrande.agedetector.service;

import pe.edu.vallegrande.agedetector.dto.ConversationRequest;
import pe.edu.vallegrande.agedetector.dto.ConversationResponse;
import pe.edu.vallegrande.agedetector.dto.MessageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationService {

    /**
     * Crea una nueva conversación con el primer mensaje
     */
    Mono<ConversationResponse> createConversation(ConversationRequest request);

    /**
     * Continúa una conversación existente agregando un nuevo mensaje
     */
    Mono<ConversationResponse> continueConversation(String conversationId, MessageRequest request);

    /**
     * Obtiene una conversación por ID con todos sus mensajes
     */
    Mono<ConversationResponse> getConversationById(String conversationId);

    /**
     * Elimina lógicamente una conversación (cambia status a "I")
     */
    Mono<Void> deleteConversation(String conversationId);

    /**
     * Restaura una conversación eliminada (cambia status a "A")
     */
    Mono<Void> restoreConversation(String conversationId);

    /**
     * Obtiene conversaciones activas (status = "A")
     */
    Flux<ConversationResponse> getActiveConversations();

    /**
     * Obtiene conversaciones inactivas (status = "I")
     */
    Flux<ConversationResponse> getInactiveConversations();
}