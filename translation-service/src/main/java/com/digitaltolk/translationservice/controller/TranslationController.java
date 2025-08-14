package com.digitaltolk.translationservice.controller;

import com.digitaltolk.translationservice.dto.TranslationDto;
import com.digitaltolk.translationservice.service.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
@Tag(name = "Translation Management", description = "Endpoints for creating, updating, searching, and exporting translations")
public class TranslationController {

    private final TranslationService translationService;

    @Operation(
            summary = "Seed database with sample translations",
            description = "Creates 100,000 dummy translation records for testing."
    )
    @PostMapping("/seed")
    public ResponseEntity<String> seedTranslations() {
        translationService.createSeedTranslations();
        return ResponseEntity.ok("Successfully seeded translations.");
    }


    @Operation(
            summary = "Create a new translation",
            description = "Creates a new translation entry with key, locale, content, and optional tags.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Translation created successfully",
                            content = @Content(schema = @Schema(implementation = TranslationDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping
    public ResponseEntity<TranslationDto> create(@Valid @RequestBody TranslationDto translation) {
        return ResponseEntity.ok(translationService.createTranslation(translation));
    }

    @Operation(
            summary = "Update an existing translation",
            description = "Updates an existing translation by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Translation updated successfully",
                            content = @Content(schema = @Schema(implementation = TranslationDto.class))),
                    @ApiResponse(responseCode = "404", description = "Translation not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TranslationDto> update(
            @Parameter(description = "Translation ID", required = true) @PathVariable String id,
            @Valid @RequestBody TranslationDto translation) {
        return ResponseEntity.ok(translationService.updateTranslation(id, translation));
    }

    @Operation(
            summary = "Get all translations",
            description = "Retrieves a list of all translations."
    )
    @GetMapping
    public ResponseEntity<List<TranslationDto>> getAll() {
        return ResponseEntity.ok(translationService.getAllTranslations());
    }

    @Operation(
            summary = "Search translations by key",
            description = "Finds translations whose keys match the provided string."
    )
    @GetMapping("/search/key")
    public ResponseEntity<List<TranslationDto>> searchByKey(
            @Parameter(description = "Key to search for") @RequestParam String key) {
        return ResponseEntity.ok(translationService.searchByKey(key));
    }

    @Operation(
            summary = "Search translations by content",
            description = "Finds translations whose content contains the given string."
    )
    @GetMapping("/search/content")
    public ResponseEntity<List<TranslationDto>> searchByContent(
            @Parameter(description = "Content to search for") @RequestParam String content) {
        return ResponseEntity.ok(translationService.searchByContent(content));
    }

    @Operation(
            summary = "Search translations by tags",
            description = "Finds translations that have any of the specified tags."
    )
    @GetMapping("/search/tags")
    public ResponseEntity<List<TranslationDto>> searchByTags(
            @Parameter(description = "Tags to search for (comma-separated)") @RequestParam Collection<String> tags) {
        return ResponseEntity.ok(translationService.searchByTags(tags));
    }

    @Operation(
            summary = "Get translations by locale",
            description = "Retrieves translations for a specific locale."
    )
    @GetMapping("/locale/{locale}")
    public ResponseEntity<List<TranslationDto>> getByLocale(
            @Parameter(description = "Locale code, e.g., en, fr, es") @PathVariable String locale) {
        return ResponseEntity.ok(translationService.getByLocale(locale));
    }

    @Operation(
            summary = "Get a translation by ID",
            description = "Retrieves a single translation by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TranslationDto> getById(
            @Parameter(description = "Translation ID", required = true) @PathVariable String id) {
        return translationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Export translations as CSV",
            description = "Exports all translations in CSV format for download."
    )
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportTranslations() {
        List<TranslationDto> allTranslations = translationService.getAllTranslations();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Key,Locale,Content,Tags,Created At,Updated At\n");

        for (TranslationDto dto : allTranslations) {
            csvBuilder
                    .append(escapeCsv(dto.getId())).append(",")
                    .append(escapeCsv(dto.getKey())).append(",")
                    .append(escapeCsv(dto.getLocale())).append(",")
                    .append(escapeCsv(dto.getContent())).append(",")
                    .append(escapeCsv(dto.getTags() != null ? String.join(";", dto.getTags()) : "")).append(",")
                    .append(escapeCsv(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "")).append(",")
                    .append(escapeCsv(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "")).append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=translations.csv")
                .header("Content-Type", "text/csv")
                .body(csvBytes);
    }

    @Operation(
            summary = "Export translations as JSON",
            description = "Exports all translations in JSON format for download."
    )
    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportTranslationsJson() throws Exception {
        List<TranslationDto> allTranslations = translationService.getAllTranslations();

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allTranslations);

        byte[] jsonBytes = jsonString.getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=translations.json")
                .header("Content-Type", "application/json")
                .body(jsonBytes);
    }

    String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
