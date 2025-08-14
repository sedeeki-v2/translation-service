package com.digitaltolk.translationservice.repository;

import com.digitaltolk.translationservice.model.Translation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TranslationRepository extends MongoRepository<Translation, String> {

    List<Translation> findByKeyContainingIgnoreCase(String key);

    List<Translation> findByContentContainingIgnoreCase(String content);

    List<Translation> findByTagsIn(Collection<String> tags);

    List<Translation> findByLocale(String locale);
}
