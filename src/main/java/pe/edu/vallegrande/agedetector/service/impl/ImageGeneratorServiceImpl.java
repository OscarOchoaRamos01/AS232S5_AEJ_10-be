package pe.edu.vallegrande.agedetector.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.agedetector.dto.ImageApiResponse;
import pe.edu.vallegrande.agedetector.dto.ImageGeneratorRequest;
import pe.edu.vallegrande.agedetector.dto.ImageGeneratorResponse;
import pe.edu.vallegrande.agedetector.service.ImageGeneratorService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ImageGeneratorServiceImpl implements ImageGeneratorService {

        private final WebClient.Builder webClientBuilder;

        public ImageGeneratorServiceImpl(WebClient.Builder webClientBuilder) {
                this.webClientBuilder = webClientBuilder;
        }

        @Value("${external-apis.rapidapi.key}")
        private String apiKey;

        @Value("${external-apis.rapidapi.image-generator.url}")
        private String imageGeneratorUrl;

        @Value("${external-apis.rapidapi.image-generator.host}")
        private String imageGeneratorHost;

        @Override
        public Mono<ImageGeneratorResponse> generateImage(String conversationId, String prompt) {
                // Este método mantiene la compatibilidad con el código existente
                // pero ya no maneja conversaciones directamente
                return generateImageOnly(prompt);
        }

        @Override
        public Mono<ImageGeneratorResponse> generateImageOnly(String prompt) {
                log.info("Generando imagen con prompt: {}", prompt);

                ImageGeneratorRequest request = ImageGeneratorRequest.builder()
                                .prompt(prompt)
                                .style_id(4) // Estilo por defecto según tu ejemplo
                                .size("1-1") // Formato cuadrado por defecto
                                .build();

                WebClient webClient = webClientBuilder.build();

                return webClient.post()
                                .uri(imageGeneratorUrl)
                                .header("X-RapidAPI-Key", apiKey)
                                .header("X-RapidAPI-Host", imageGeneratorHost)
                                .header("Content-Type", "application/json")
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(ImageApiResponse.class)
                                .doOnNext(apiResponse -> {
                                        log.info("📊 Respuesta completa de la API: code={}, message={}, result={}",
                                                        apiResponse.getCode(),
                                                        apiResponse.getMessage(),
                                                        apiResponse.getResult() != null ? "presente" : "null");
                                })
                                .map(apiResponse -> {
                                        // Extraer la URL de la imagen de la respuesta compleja
                                        String imageUrl = null;
                                        if (apiResponse.getResult() != null &&
                                                        apiResponse.getResult().getData() != null &&
                                                        apiResponse.getResult().getData().getResults() != null &&
                                                        !apiResponse.getResult().getData().getResults().isEmpty()) {

                                                // Buscar una imagen que no sea NSFW, si no hay, tomar la primera
                                                imageUrl = apiResponse.getResult().getData().getResults().stream()
                                                                .filter(result -> !result.isNsfw())
                                                                .findFirst()
                                                                .map(result -> result.getOrigin())
                                                                .orElse(apiResponse.getResult().getData().getResults()
                                                                                .get(0).getOrigin());

                                                log.info("✅ Imagen seleccionada de {} opciones disponibles: {}",
                                                                apiResponse.getResult().getData().getResults().size(),
                                                                imageUrl);
                                        }

                                        return ImageGeneratorResponse.builder()
                                                        .image_url(imageUrl)
                                                        .status(apiResponse.getCode() == 200 ? "success" : "error")
                                                        .message(apiResponse.getMessage())
                                                        .success(apiResponse.getCode() == 200 && imageUrl != null)
                                                        .build();
                                })
                                .doOnSuccess(response -> log.info("Imagen generada exitosamente: {}",
                                                response.isSuccess()))
                                .onErrorResume(throwable -> {
                                        log.error("Error generando imagen - Detalles: {}", throwable.getMessage(),
                                                        throwable);

                                        String errorMessage = "Error al generar imagen";
                                        if (throwable.getMessage() != null) {
                                                if (throwable.getMessage().contains("Failed to get task ID")) {
                                                        errorMessage = "API de imágenes temporalmente no disponible";
                                                } else if (throwable.getMessage().contains("timeout")) {
                                                        errorMessage = "Timeout en la API de imágenes";
                                                } else if (throwable.getMessage().contains("429")) {
                                                        errorMessage = "Límite de requests alcanzado, intenta más tarde";
                                                } else {
                                                        errorMessage = "Error al generar imagen: "
                                                                        + throwable.getMessage();
                                                }
                                        }

                                        ImageGeneratorResponse errorResponse = ImageGeneratorResponse.builder()
                                                        .success(false)
                                                        .message(errorMessage)
                                                        .status("error")
                                                        .build();
                                        return Mono.just(errorResponse);
                                });
        }
}