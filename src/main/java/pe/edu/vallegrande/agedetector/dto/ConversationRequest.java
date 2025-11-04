package pe.edu.vallegrande.agedetector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRequest {

    @NotBlank(message = "El contenido del mensaje es requerido")
    private String content;

    @NotBlank(message = "El tipo de servicio es requerido")
    private String serviceType; // "CHATGPT" o "IMAGE"

    private String title; // Título opcional para la conversación
}