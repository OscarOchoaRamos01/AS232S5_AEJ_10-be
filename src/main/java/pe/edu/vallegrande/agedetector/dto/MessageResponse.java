package pe.edu.vallegrande.agedetector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.agedetector.model.entity.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String id;
    private MessageType type;
    private String content;
    private String imageUrl;
    private LocalDateTime timestamp;
}