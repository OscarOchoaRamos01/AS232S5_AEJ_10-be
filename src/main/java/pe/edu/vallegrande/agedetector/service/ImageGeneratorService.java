package pe.edu.vallegrande.agedetector.service;

import pe.edu.vallegrande.agedetector.dto.ImageGeneratorResponse;
import reactor.core.publisher.Mono;

public interface ImageGeneratorService {

    Mono<ImageGeneratorResponse> generateImage(String conversationId, String prompt);

    Mono<ImageGeneratorResponse> generateImageOnly(String prompt);
}
