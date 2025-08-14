package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.dto.TranslationDto;
import com.digitaltolk.translationservice.exception.ResourceNotFoundException;
import com.digitaltolk.translationservice.model.Translation;
import com.digitaltolk.translationservice.repository.TranslationRepository;
import com.digitaltolk.translationservice.util.ModelMapperWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final ModelMapperWrapper modelMapperWrapper;

    public TranslationDto createTranslation(TranslationDto translation) {

        Translation entity = modelMapperWrapper.map(translation, Translation.class);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return modelMapperWrapper.map(translationRepository.save(entity), TranslationDto.class);
    }

    public TranslationDto updateTranslation(String id, TranslationDto updated) {
        Optional<Translation> optional = translationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Translation not found with id: " + id);
        }

        Translation existing = optional.get();
        modelMapperWrapper.map(updated, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        return modelMapperWrapper.map(translationRepository.save(existing), TranslationDto.class);
    }


    public List<TranslationDto> getAllTranslations() {
        List<Translation> translations = translationRepository.findAll();
        return modelMapperWrapper.mapList(translations, TranslationDto.class);
    }

    public List<TranslationDto> searchByKey(String key) {
        List<Translation> translations = translationRepository.findByKeyContainingIgnoreCase(key);
        return modelMapperWrapper.mapList(translations, TranslationDto.class);
    }

    public List<TranslationDto> searchByContent(String content) {
        List<Translation> translations = translationRepository.findByContentContainingIgnoreCase(content);
        return modelMapperWrapper.mapList(translations, TranslationDto.class);
    }

    public List<TranslationDto> searchByTags(Collection<String> tags) {
        List<Translation> translations = translationRepository.findByTagsIn(tags);
        return modelMapperWrapper.mapList(translations, TranslationDto.class);
    }

    public List<TranslationDto> getByLocale(String locale) {
        List<Translation> translations = translationRepository.findByLocale(locale);
        return modelMapperWrapper.mapList(translations, TranslationDto.class);
    }

    public Optional<TranslationDto> getById(String id) {
        return translationRepository.findById(id)
                .map(translation -> modelMapperWrapper.map(translation, TranslationDto.class));
    }

    public void createSeedTranslations() {
        int total = 100_000;
        int batchSize = 1000;

        List<Translation> batch = new ArrayList<>(batchSize);

        for (int i = 1; i <= total; i++) {
            Translation translation = new Translation();
            translation.setKey("key_" + i);
            translation.setLocale(i % 2 == 0 ? "en" : "fr");
            translation.setContent("Sample content for translation " + i);

            Set<String> tags = new HashSet<>();
            tags.add("tag" + (i % 10));
            tags.add("tag" + (i % 20));
            translation.setTags(tags);

            translation.setCreatedAt(LocalDateTime.now());
            translation.setUpdatedAt(LocalDateTime.now());

            batch.add(translation);

            if (batch.size() == batchSize) {
                translationRepository.saveAll(batch);
                batch.clear();
                log.info("Inserted {} records...", i);
            }
        }

        if (!batch.isEmpty()) {
            translationRepository.saveAll(batch);
        }

        log.info("Finished inserting {} records.", total);
    }


}
