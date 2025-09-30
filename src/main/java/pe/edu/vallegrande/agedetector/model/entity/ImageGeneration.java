package pe.edu.vallegrande.agedetector.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private boolean success;
    private String status;
    private String message;
    private String errorMessage;
}
