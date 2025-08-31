package pe.edu.vallegrande.agedetector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGeneratorRequest {

    private String prompt;
    private int style_id;
    private String size;
}
