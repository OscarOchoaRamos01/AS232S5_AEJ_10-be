package pe.edu.vallegrande.agedetector.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.agedetector.model.entity.ImageGeneration;
import reactor.core.publisher.Flux;

@Repository
public interface ImageGenerationRepository extends ReactiveMongoRepository<ImageGeneration, String> {
    Flux<ImageGeneration> findAllByOrderByTimestampDesc();
}
