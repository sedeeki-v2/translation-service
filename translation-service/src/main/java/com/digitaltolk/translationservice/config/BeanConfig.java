package com.digitaltolk.translationservice.config;

import com.digitaltolk.translationservice.model.Translation;
import com.digitaltolk.translationservice.dto.TranslationDto;
import com.digitaltolk.translationservice.util.ModelMapperWrapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<TranslationDto, Translation>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedAt());
            }
        });

        return modelMapper;
    }

    @Bean
    public ModelMapperWrapper modelMapperWrapper(ModelMapper modelMapper) {
        return new ModelMapperWrapper(modelMapper);
    }
}