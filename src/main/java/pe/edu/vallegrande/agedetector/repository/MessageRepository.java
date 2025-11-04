package pe.edu.vallegrande.agedetector.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.agedetector.model.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    Flux<Message> findByConversationId(String conversationId);

    Flux<Message> findByConversationIdOrderByTimestampAsc(String conversationId);

    Mono<Long> countByConversationId(String conversationId);
}
