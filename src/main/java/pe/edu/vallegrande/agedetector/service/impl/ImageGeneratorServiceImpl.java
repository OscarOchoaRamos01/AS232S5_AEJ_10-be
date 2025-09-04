package pe.edu.vallegrande.agedetector.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.agedetector.model.ImageGeneratorRequest;
import pe.edu.vallegrande.agedetector.model.ImageGeneratorResponse;
import pe.edu.vallegrande.agedetector.model.ImageApiResponse;
import pe.edu.vallegrande.agedetector.model.entity.ImageGeneration;
import pe.edu.vallegrande.agedetector.repository.ImageGenerationRepository;
import pe.edu.vallegrande.agedetector.service.ImageGeneratorService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ImageGeneratorServiceImpl implements ImageGeneratorService {

        private final WebClient webClient;
        private final ImageGenerationRepository imageGenerationRepository;
        private final String apiKey;
        private final String imageGeneratorUrl;
        private final String imageGeneratorHost;

        public ImageGeneratorServiceImpl(WebClient.Builder webClientBuilder,
                        ImageGenerationRepository imageGenerationRepository,
                        @Value("${external-apis.rapidapi.key}") String apiKey,
                        @Value("${external-apis.rapidapi.image-generator.url}") String imageGeneratorUrl,
                        @Value("${external-apis.rapidapi.image-generator.host}") String imageGeneratorHost) {
                this.webClient = webClientBuilder.build();
                this.imageGenerationRepository = imageGenerationRepository;
                this.apiKey = apiKey;
                this.imageGeneratorUrl = imageGeneratorUrl;
                this.imageGeneratorHost = imageGeneratorHost;
        }

        @Override
        public Mono<ImageGeneratorResponse> generateImage(String prompt) {
                ImageGeneratorRequest request = ImageGeneratorRequest.builder()
                                .prompt(prompt)
                                .style_id(4)
                                .size("1-1")
                                .build();

                return webClient.post()
                                .uri(imageGeneratorUrl)
                                .header("X-RapidAPI-Key", apiKey)
                                .header("X-RapidAPI-Host", imageGeneratorHost)
                                .header("Content-Type", "application/json")
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(ImageApiResponse.class)
                                .doOnNext(apiResponse -> {
                                        System.out.println("🔍 API RESPONSE: " + apiResponse);
                                })
                                .map(apiResponse -> {
                                        // Convertir ImageApiResponse a ImageGeneratorResponse
                                        if (apiResponse != null && apiResponse.getCode() == 200
                                                        && apiResponse.getResult() != null
                                                        && apiResponse.getResult().getData() != null
                                                        && apiResponse.getResult().getData().getResults() != null
                                                        && !apiResponse.getResult().getData().getResults().isEmpty()) {
                                                String imageUrl = apiResponse.getResult().getData().getResults().get(0)
                                                                .getOrigin();
                                                return ImageGeneratorResponse.builder()
                                                                .image_url(imageUrl)
                                                                .status("success")
                                                                .message("Image generated successfully")
                                                                .success(true)
                                                                .build();
                                        } else {
                                                return ImageGeneratorResponse.builder()
                                                                .image_url(null)
                                                                .status("error")
                                                                .message("No images generated")
                                                                .success(false)
                                                                .build();
                                        }
                                })
                                .flatMap(response -> {
                                        // Guardar la generación de imagen en la base de datos
                                        ImageGeneration imageGeneration = ImageGeneration.builder()
                                                        .prompt(request.getPrompt())
                                                        .imageUrl(response.getImage_url())
                                                        .styleId(request.getStyle_id())
                                                        .size(request.getSize())
                                                        .timestamp(LocalDateTime.now())
                                                        .success(response.isSuccess())
                                                        .status(response.getStatus())
                                                        .message(response.getMessage())
                                                        .build();

                                        return imageGenerationRepository.save(imageGeneration)
                                                        .thenReturn(response);
                                })
                                .onErrorResume(throwable -> {
                                        System.out.println("❌ ERROR: " + throwable.getMessage());

                                        // Guardar el error en la base de datos
                                        ImageGeneration imageGeneration = ImageGeneration.builder()
                                                        .prompt(request.getPrompt())
                                                        .styleId(request.getStyle_id())
                                                        .size(request.getSize())
                                                        .timestamp(LocalDateTime.now())
                                                        .success(false)
                                                        .status("error")
                                                        .errorMessage("Error al generar imagen: "
                                                                        + throwable.getMessage())
                                                        .build();

                                        ImageGeneratorResponse errorResponse = ImageGeneratorResponse.builder()
                                                        .success(false)
                                                        .message("Error al generar imagen: " + throwable.getMessage())
                                                        .status("error")
                                                        .build();

                                        return imageGenerationRepository.save(imageGeneration)
                                                        .thenReturn(errorResponse);
                                });
        }
}
