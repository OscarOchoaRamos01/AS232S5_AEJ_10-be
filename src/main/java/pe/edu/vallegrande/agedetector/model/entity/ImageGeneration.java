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
@Document(collection = "image_generations")
public class ImageGeneration {

    @Id
    private String id;

    private String prompt;
    private String imageUrl;
    private int styleId;
    private String size;
    private LocalDateTime timestamp;
    private boolean success;
    private String status;
    private String message;
    private String errorMessage;
}
