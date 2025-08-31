package pe.edu.vallegrande.agedetector.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.agedetector.model.ChatGptResponse;
import pe.edu.vallegrande.agedetector.service.ChatGptService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chatgpt")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatGptController {

    private final ChatGptService chatGptService;

    @PostMapping("/ask")
    public Mono<ChatGptResponse> askQuestion(@RequestBody String question) {
        return chatGptService.sendMessage(question);
    }
}
