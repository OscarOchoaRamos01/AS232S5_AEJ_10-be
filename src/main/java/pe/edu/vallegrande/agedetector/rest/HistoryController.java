package pe.edu.vallegrande.agedetector.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.agedetector.model.entity.ChatConversation;
import pe.edu.vallegrande.agedetector.model.entity.ImageGeneration;
import pe.edu.vallegrande.agedetector.repository.ChatConversationRepository;
import pe.edu.vallegrande.agedetector.repository.ImageGenerationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final ChatConversationRepository chatConversationRepository;
    private final ImageGenerationRepository imageGenerationRepository;

    public HistoryController(ChatConversationRepository chatConversationRepository,
            ImageGenerationRepository imageGenerationRepository) {
        this.chatConversationRepository = chatConversationRepository;
        this.imageGenerationRepository = imageGenerationRepository;
    }

    @GetMapping("/all")
    public Flux<Map<String, Object>> getAllHistory() {
        // Combinar ambos historiales en un solo flujo
        Flux<Map<String, Object>> chatHistory = chatConversationRepository.findAllByOrderByTimestampDesc()
                .map(chat -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "chat");
                    item.put("id", chat.getId());
                    item.put("question", chat.getQuestion());
                    item.put("response", chat.getResponse());
                    // Convertir LocalDateTime a string usando el mismo formato que @JsonFormat
                    item.put("timestamp", chat.getTimestamp()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                    item.put("success", chat.isSuccess());
                    return item;
                });

        Flux<Map<String, Object>> imageHistory = imageGenerationRepository.findAllByOrderByTimestampDesc()
                .map(image -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "image");
                    item.put("id", image.getId());
                    item.put("prompt", image.getPrompt());
                    item.put("imageUrl", image.getImageUrl());
                    // Convertir LocalDateTime a string usando el mismo formato que @JsonFormat
                    item.put("timestamp", image.getTimestamp()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                    item.put("success", image.isSuccess());
                    item.put("status", image.getStatus());
                    item.put("message", image.getMessage());
                    return item;
                });

        // Mergear ambos flujos y ordenar por timestamp descendente
        return Flux.merge(chatHistory, imageHistory)
                .sort((a, b) -> {
                    // Ordenar por timestamp descendente (los strings en formato ISO se pueden
                    // comparar directamente)
                    String timestampA = (String) a.get("timestamp");
                    String timestampB = (String) b.get("timestamp");
                    return timestampB.compareTo(timestampA);
                });
    }

    @GetMapping("/chat")
    public Flux<ChatConversation> getChatHistory() {
        return chatConversationRepository.findAllByOrderByTimestampDesc();
    }

    @GetMapping("/images")
    public Flux<ImageGeneration> getImageHistory() {
        return imageGenerationRepository.findAllByOrderByTimestampDesc();
    }

    @GetMapping("/chat/{id}")
    public Mono<ChatConversation> getChatById(@PathVariable String id) {
        return chatConversationRepository.findById(id);
    }

    @GetMapping("/image/{id}")
    public Mono<ImageGeneration> getImageById(@PathVariable String id) {
        return imageGenerationRepository.findById(id);
    }
}
