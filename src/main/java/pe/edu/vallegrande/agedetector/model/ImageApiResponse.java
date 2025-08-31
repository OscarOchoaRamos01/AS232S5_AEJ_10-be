package pe.edu.vallegrande.agedetector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageApiResponse {

    private List<ImageResult> final_result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageResult {
        private int index;
        private boolean nsfw;
        private String origin;
        private String thumb;
    }
}
