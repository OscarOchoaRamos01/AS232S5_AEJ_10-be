package pe.edu.vallegrande.agedetector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGeneratorResponse {

    private String image_url;
    private String status;
    private String message;
    private boolean success;
}
