package pe.edu.vallegrande.agedetector.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.agedetector.model.entity.ImageGeneration;

@Repository
public interface ImageGenerationRepository extends ReactiveMongoRepository<ImageGeneration, String> {
}
