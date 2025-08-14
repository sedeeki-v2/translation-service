package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.dto.TranslationDto;
import com.digitaltolk.translationservice.exception.ResourceNotFoundException;
import com.digitaltolk.translationservice.model.Translation;
import com.digitaltolk.translationservice.repository.TranslationRepository;
import com.digitaltolk.translationservice.util.ModelMapperWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TranslationServiceTest {

    @Mock
    private TranslationRepository repository;

    @Mock
    private ModelMapperWrapper mapper;

    @InjectMocks
    private TranslationService service;

    private Translation translation;
    private TranslationDto translationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        translation = new Translation();
        translation.setId("1");
        translation.setKey("greeting");
        translation.setLocale("en");
        translation.setContent("Hello");
        translation.setCreatedAt(LocalDateTime.now());
        translation.setUpdatedAt(LocalDateTime.now());

        translationDto = new TranslationDto();
        translationDto.setId("1");
        translationDto.setKey("greeting");
        translationDto.setLocale("en");
        translationDto.setContent("Hello");
    }

    @Test
    void createTranslation_ShouldSaveAndReturnDto() {
        when(mapper.map(translationDto, Translation.class)).thenReturn(translation);
        when(repository.save(any(Translation.class))).thenReturn(translation);
        when(mapper.map(any(Translation.class), eq(TranslationDto.class))).thenReturn(translationDto);

        TranslationDto result = service.createTranslation(translationDto);

        assertEquals("greeting", result.getKey());
        verify(repository, times(1)).save(any(Translation.class));
    }

    @Test
    void updateTranslation_ShouldUpdate_WhenFound() {
        when(repository.findById("1")).thenReturn(Optional.of(translation));
        when(repository.save(any())).thenReturn(translation);
        when(mapper.map(any(Translation.class), eq(TranslationDto.class))).thenReturn(translationDto);

        TranslationDto result = service.updateTranslation("1", translationDto);

        assertEquals("greeting", result.getKey());
        verify(repository).save(translation);
    }

    @Test
    void updateTranslation_ShouldThrow_WhenNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                service.updateTranslation("1", translationDto)
        );
    }

    @Test
    void getAllTranslations_ShouldReturnMappedList() {
        when(repository.findAll()).thenReturn(List.of(translation));
        when(mapper.mapList(anyList(), eq(TranslationDto.class))).thenReturn(List.of(translationDto));

        List<TranslationDto> result = service.getAllTranslations();

        assertEquals(1, result.size());
    }

    @Test
    void searchByKey_ShouldReturnMappedList() {
        when(repository.findByKeyContainingIgnoreCase("hello")).thenReturn(List.of(translation));
        when(mapper.mapList(anyList(), eq(TranslationDto.class))).thenReturn(List.of(translationDto));

        List<TranslationDto> result = service.searchByKey("hello");

        assertEquals(1, result.size());
        verify(repository).findByKeyContainingIgnoreCase("hello");
    }

    @Test
    void searchByContent_ShouldReturnMappedList() {
        when(repository.findByContentContainingIgnoreCase("greeting")).thenReturn(List.of(translation));
        when(mapper.mapList(anyList(), eq(TranslationDto.class))).thenReturn(List.of(translationDto));

        List<TranslationDto> result = service.searchByContent("greeting");

        assertEquals(1, result.size());
        verify(repository).findByContentContainingIgnoreCase("greeting");
    }

    @Test
    void searchByTags_ShouldReturnMappedList() {
        when(repository.findByTagsIn(List.of("welcome"))).thenReturn(List.of(translation));
        when(mapper.mapList(anyList(), eq(TranslationDto.class))).thenReturn(List.of(translationDto));

        List<TranslationDto> result = service.searchByTags(List.of("welcome"));

        assertEquals(1, result.size());
        verify(repository).findByTagsIn(List.of("welcome"));
    }

    @Test
    void getByLocale_ShouldReturnMappedList() {
        when(repository.findByLocale("en")).thenReturn(List.of(translation));
        when(mapper.mapList(anyList(), eq(TranslationDto.class))).thenReturn(List.of(translationDto));

        List<TranslationDto> result = service.getByLocale("en");

        assertEquals(1, result.size());
        verify(repository).findByLocale("en");
    }

    @Test
    void getById_ShouldReturnOptional_WhenFound() {
        when(repository.findById("1")).thenReturn(Optional.of(translation));
        when(mapper.map(any(Translation.class), eq(TranslationDto.class))).thenReturn(translationDto);

        Optional<TranslationDto> result = service.getById("1");

        assertTrue(result.isPresent());
        assertEquals("greeting", result.get().getKey());
    }

    @Test
    void getById_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        Optional<TranslationDto> result = service.getById("1");

        assertTrue(result.isEmpty());
    }
}
