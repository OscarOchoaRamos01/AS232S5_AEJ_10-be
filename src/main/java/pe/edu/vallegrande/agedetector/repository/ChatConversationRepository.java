package pe.edu.vallegrande.agedetector.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.agedetector.model.entity.ChatConversation;
import reactor.core.publisher.Flux;

@Repository
public interface ChatConversationRepository extends ReactiveMongoRepository<ChatConversation, String> {
    Flux<ChatConversation> findAllByOrderByTimestampDesc();
}
