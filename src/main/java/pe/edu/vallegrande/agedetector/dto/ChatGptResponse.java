package pe.edu.vallegrande.agedetector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptResponse {

    private String result;
    private String content;
    private boolean success;
    private String error;
}
