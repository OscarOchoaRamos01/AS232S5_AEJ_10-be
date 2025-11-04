package pe.edu.vallegrande.agedetector.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.agedetector.model.entity.Conversation;
import reactor.core.publisher.Flux;

public interface ConversationRepository extends ReactiveMongoRepository<Conversation, String> {

    Flux<Conversation> findAllByOrderByUpdatedAtDesc();

    Flux<Conversation> findByStatusOrderByUpdatedAtDesc(String status);
}