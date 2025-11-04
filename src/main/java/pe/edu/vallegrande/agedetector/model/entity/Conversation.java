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
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    private String title; // Título opcional de la conversación

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // Metadatos adicionales
    private Integer messageCount;

    private String lastMessageType; // Último tipo de mensaje (CHATGPT o IMAGE)

    @Builder.Default
    private String status = "A"; // A = Activo, I = Inactivo (eliminado lógicamente)
}