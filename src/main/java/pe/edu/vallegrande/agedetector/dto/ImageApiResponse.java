package pe.edu.vallegrande.agedetector.dto;

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

    private int code;
    private String message;
    private ResultWrapper result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResultWrapper {
        private DataWrapper data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataWrapper {
        private String prompt_id;
        private List<ImageResult> results;
    }

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
