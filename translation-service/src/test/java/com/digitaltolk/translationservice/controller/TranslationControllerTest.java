package com.digitaltolk.translationservice.controller;

import com.digitaltolk.translationservice.config.SecurityConfig;
import com.digitaltolk.translationservice.dto.TranslationDto;
import com.digitaltolk.translationservice.security.JwtAuthenticationFilter;
import com.digitaltolk.translationservice.security.JwtUtil;
import com.digitaltolk.translationservice.service.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TranslationController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtil.class)
})
@AutoConfigureMockMvc(addFilters = false)
class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranslationService translationService;

    @Autowired
    private ObjectMapper objectMapper;

    private TranslationDto dto;

    @BeforeEach
    void setUp() {
        dto = new TranslationDto();
        dto.setId("123");
        dto.setKey("greeting");
        dto.setContent("Hello");
        dto.setLocale("en");
        dto.setTags(Set.of("tag1"));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void create_ShouldReturnTranslation() throws Exception {
        TranslationDto dto = new TranslationDto();
        dto.setId("1");
        dto.setKey("greeting");
        dto.setLocale("en");
        dto.setContent("Hello");

        Mockito.when(translationService.createTranslation(any(TranslationDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/translations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("greeting"));
    }

    @Test
    void getById_ShouldReturnTranslation_WhenFound() throws Exception {
        TranslationDto dto = new TranslationDto();
        dto.setId("1");
        dto.setKey("greeting");

        Mockito.when(translationService.getById("1")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/translations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("greeting"));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        Mockito.when(translationService.getById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/translations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        TranslationDto dto = new TranslationDto();
        dto.setId("1");
        dto.setKey("greeting");

        Mockito.when(translationService.getAllTranslations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("greeting"));
    }

    @Test
    void update_ShouldReturnUpdatedTranslation() throws Exception {
        TranslationDto updatedDto = new TranslationDto();
        updatedDto.setId("1");
        updatedDto.setKey("greeting_updated");
        updatedDto.setLocale("en");
        updatedDto.setContent("Hello Updated");

        when(translationService.updateTranslation(Mockito.eq("1"), any(TranslationDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/api/translations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("greeting_updated"))
                .andExpect(jsonPath("$.content").value("Hello Updated"));
    }

    @Test
    void searchByKey_ShouldReturnResults() throws Exception {
        when(translationService.searchByKey("greet")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/search/key")
                        .param("key", "greet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("greeting"));
    }

    @Test
    void searchByContent_ShouldReturnResults() throws Exception {
        when(translationService.searchByContent("Hello")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/search/content")
                        .param("content", "Hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }

    @Test
    void searchByTags_ShouldReturnResults() throws Exception {
        when(translationService.searchByTags(Set.of("tag1"))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/search/tags")
                        .param("tags", "tag1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tags[0]").value("tag1"));
    }

    @Test
    void getByLocale_ShouldReturnResults() throws Exception {
        when(translationService.getByLocale("en")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/locale/en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locale").value("en"));
    }

    @Test
    void exportTranslations_ShouldReturnCsvFile() throws Exception {
        when(translationService.getAllTranslations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=translations.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("greeting")));
    }

    @Test
    void exportTranslations_ShouldReturnCsvFile_WithNullFields() throws Exception {
        TranslationDto dtoWithNulls = new TranslationDto();
        dtoWithNulls.setId("id2");
        dtoWithNulls.setKey("farewell");
        dtoWithNulls.setLocale("fr");
        dtoWithNulls.setContent("au revoir");
        dtoWithNulls.setTags(null);
        dtoWithNulls.setCreatedAt(null);
        dtoWithNulls.setUpdatedAt(null);

        when(translationService.getAllTranslations())
                .thenReturn(List.of(dtoWithNulls));

        mockMvc.perform(get("/api/translations/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=translations.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("farewell")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("fr")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(",,,")));
    }

    @Test
    void exportTranslationsJson_ShouldReturnJsonFile() throws Exception {
        when(translationService.getAllTranslations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/translations/export/json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=translations.json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("greeting")));
    }

    @Test
    void escapeCsv_ShouldReturnEmptyString_WhenValueIsNull() {
        TranslationController controller = new TranslationController(null);
        String result = controller.escapeCsv(null);
        assertEquals("", result);
    }

    @Test
    void escapeCsv_ShouldEscapeQuotes_WhenValueContainsQuote() {
        TranslationController controller = new TranslationController(null);
        String result = controller.escapeCsv("he said \"hello\"");
        assertEquals("\"he said \"\"hello\"\"\"", result);
    }

    @Test
    void escapeCsv_ShouldWrapInQuotes_WhenValueContainsCommaOrNewline() {
        TranslationController controller = new TranslationController(null);
        String resultWithComma = controller.escapeCsv("hello,world");
        assertEquals("\"hello,world\"", resultWithComma);

        String resultWithNewline = controller.escapeCsv("hello\nworld");
        assertEquals("\"hello\nworld\"", resultWithNewline);
    }
}
