package com.digitaltolk.translationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a translation entry in the system")
public class TranslationDto {

    @Schema(description = "Unique identifier of the translation", example = "123")
    private String id;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Translation key used for lookup", example = "greeting.hello")
    private String key;

    @NotBlank
    @Schema(description = "The actual translation text", example = "Hello")
    private String content;

    @NotBlank
    @Schema(description = "Locale code for the translation", example = "en")
    private String locale;

    @Schema(description = "Tags associated with the translation", example = "[\"welcome\",\"homepage\"]")
    private Set<String> tags;

    @Schema(description = "Date and time when the translation was created", example = "2025-08-14T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the translation was last updated", example = "2025-08-14T12:45:00")
    private LocalDateTime updatedAt;
}
