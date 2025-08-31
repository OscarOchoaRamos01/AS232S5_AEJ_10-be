package pe.edu.vallegrande.agedetector.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.agedetector.model.ImageGeneratorResponse;
import pe.edu.vallegrande.agedetector.service.ImageGeneratorService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/image-generator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageGeneratorController {

    private final ImageGeneratorService imageGeneratorService;

    @PostMapping("/generate")
    public Mono<ImageGeneratorResponse> generateImage(@RequestBody String prompt) {
        return imageGeneratorService.generateImage(prompt);
    }
}
