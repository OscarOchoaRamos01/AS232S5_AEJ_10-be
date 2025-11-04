package pe.edu.vallegrande.agedetector.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String conversationId;

    private MessageType type;

    private String content;

    private String imageUrl; // Para respuestas de imágenes

    private LocalDateTime timestamp;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Campos existentes para compatibilidad
    private boolean success;

    private String errorMessage;

    private String question;

    private String response;

    private boolean webAccess;
}