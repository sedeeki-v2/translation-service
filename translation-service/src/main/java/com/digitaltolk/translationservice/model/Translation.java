package com.digitaltolk.translationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "translations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Translation {
    @Id
    private String id;

    @Indexed
    private String key;

    @Indexed
    private String locale;

    @Indexed
    private String content;

    @Indexed
    private Set<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

