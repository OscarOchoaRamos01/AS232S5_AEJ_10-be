package pe.edu.vallegrande.agedetector.service;

import pe.edu.vallegrande.agedetector.model.ImageGeneratorResponse;
import reactor.core.publisher.Mono;

public interface ImageGeneratorService {

    Mono<ImageGeneratorResponse> generateImage(String prompt);
}
