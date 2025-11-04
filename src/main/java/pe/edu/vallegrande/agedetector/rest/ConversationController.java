package pe.edu.vallegrande.agedetector.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.agedetector.dto.ConversationRequest;
import pe.edu.vallegrande.agedetector.dto.ConversationResponse;
import pe.edu.vallegrande.agedetector.dto.MessageRequest;
import pe.edu.vallegrande.agedetector.service.ConversationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {

    private final ConversationService conversationService;

    // ========================================
    // API 1: CHATGPT (NUEVA CONVERSACIÓN)
    // ========================================
    @PostMapping("/chatgpt")
    public Mono<ResponseEntity<ConversationResponse>> chatGptNewConversation(
            @RequestBody String message) {

        log.info("🤖 CHATGPT - NUEVA CONVERSACIÓN: '{}'",
                message.length() > 50 ? message.substring(0, 50) + "..." : message);

        ConversationRequest request = ConversationRequest.builder()
                .content(message)
                .serviceType("CHATGPT")
                .build();

        return conversationService.createConversation(request)
                .map(response -> {
                    log.info("✅ CONVERSACIÓN CHATGPT CREADA - ID: {}", response.getConversationId());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR CHATGPT: {}", error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    // ========================================
    // API 2: IMÁGENES (NUEVA CONVERSACIÓN)
    // ========================================
    @PostMapping("/images")
    public Mono<ResponseEntity<ConversationResponse>> imagesNewConversation(
            @RequestBody String prompt) {

        log.info("🖼️ IMÁGENES - NUEVA CONVERSACIÓN: '{}'",
                prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt);

        ConversationRequest request = ConversationRequest.builder()
                .content(prompt)
                .serviceType("IMAGE")
                .build();

        return conversationService.createConversation(request)
                .map(response -> {
                    log.info("✅ CONVERSACIÓN IMÁGENES CREADA - ID: {}", response.getConversationId());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR IMÁGENES: {}", error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    // ========================================
    // API 3: CHATGPT (CONVERSACIÓN EXISTENTE)
    // ========================================
    @PostMapping("/chatgpt/{conversationId}")
    public Mono<ResponseEntity<ConversationResponse>> chatGptExistingConversation(
            @PathVariable String conversationId,
            @RequestBody String message) {

        log.info("🤖 CHATGPT - CONVERSACIÓN {}: '{}'", conversationId,
                message.length() > 50 ? message.substring(0, 50) + "..." : message);

        MessageRequest request = MessageRequest.builder()
                .content(message)
                .serviceType("CHATGPT")
                .build();

        return conversationService.continueConversation(conversationId, request)
                .map(response -> {
                    log.info("✅ MENSAJE CHATGPT AGREGADO - ID: {}", conversationId);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR CHATGPT CONVERSACIÓN {}: {}", conversationId, error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    // ========================================
    // API 4: IMÁGENES (CONVERSACIÓN EXISTENTE)
    // ========================================
    @PostMapping("/images/{conversationId}")
    public Mono<ResponseEntity<ConversationResponse>> imagesExistingConversation(
            @PathVariable String conversationId,
            @RequestBody String prompt) {

        log.info("�️ IMÁGENES - CONVERSACIÓN {}: '{}'", conversationId,
                prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt);

        MessageRequest request = MessageRequest.builder()
                .content(prompt)
                .serviceType("IMAGE")
                .build();

        return conversationService.continueConversation(conversationId, request)
                .map(response -> {
                    log.info("✅ IMAGEN AGREGADA - ID: {}", conversationId);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR IMÁGENES CONVERSACIÓN {}: {}", conversationId, error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    // ========================================
    // API 5: LISTAR CONVERSACIÓN
    // ========================================
    @GetMapping("/conversations/{conversationId}")
    public Mono<ResponseEntity<ConversationResponse>> getConversation(
            @PathVariable String conversationId) {

        log.info("📖 LISTANDO CONVERSACIÓN: {}", conversationId);

        return conversationService.getConversationById(conversationId)
                .map(response -> {
                    log.info("✅ CONVERSACIÓN ENCONTRADA - ID: {}, Mensajes: {}",
                            conversationId, response.getMessageCount());
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // ========================================
    // API 6: ELIMINAR CONVERSACIÓN (LÓGICO)
    // ========================================
    @DeleteMapping("/conversations/{conversationId}")
    public Mono<ResponseEntity<Void>> deleteConversation(
            @PathVariable String conversationId) {

        log.info("🗑️ ELIMINANDO CONVERSACIÓN: {}", conversationId);

        return conversationService.deleteConversation(conversationId)
                .map(unused -> {
                    log.info("✅ CONVERSACIÓN ELIMINADA - ID: {}", conversationId);
                    return ResponseEntity.ok().<Void>build();
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR ELIMINANDO CONVERSACIÓN {}: {}", conversationId, error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    // ========================================
    // API 7: RESTAURAR CONVERSACIÓN
    // ========================================
    @PostMapping("/conversations/{conversationId}/restore")
    public Mono<ResponseEntity<Void>> restoreConversation(
            @PathVariable String conversationId) {

        log.info("🔄 RESTAURANDO CONVERSACIÓN: {}", conversationId);

        return conversationService.restoreConversation(conversationId)
                .map(unused -> {
                    log.info("✅ CONVERSACIÓN RESTAURADA - ID: {}", conversationId);
                    return ResponseEntity.ok().<Void>build();
                })
                .onErrorResume(error -> {
                    log.error("❌ ERROR RESTAURANDO CONVERSACIÓN {}: {}", conversationId, error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    // ========================================
    // API 8: LISTAR CONVERSACIONES ACTIVAS
    // ========================================
    @GetMapping("/conversations/active")
    public Flux<ConversationResponse> getActiveConversations() {
        log.info("📋 LISTANDO CONVERSACIONES ACTIVAS");
        return conversationService.getActiveConversations();
    }

    // ========================================
    // API 9: LISTAR CONVERSACIONES INACTIVAS
    // ========================================
    @GetMapping("/conversations/inactive")
    public Flux<ConversationResponse> getInactiveConversations() {
        log.info("🗑️ LISTANDO CONVERSACIONES INACTIVAS");
        return conversationService.getInactiveConversations();
    }
}