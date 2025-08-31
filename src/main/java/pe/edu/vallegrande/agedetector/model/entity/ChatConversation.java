package pe.edu.vallegrande.agedetector.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_conversations")
public class ChatConversation {

    @Id
    private String id;

    private String question;
    private String response;
    private LocalDateTime timestamp;
    private boolean webAccess;
    private boolean success;
    private String errorMessage;
}
