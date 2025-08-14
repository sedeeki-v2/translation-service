package com.digitaltolk.translationservice.util;

import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperWrapper {

    private final ModelMapper modelMapper;

    public ModelMapperWrapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <S, D> void map(final S source, final D destination) {
        modelMapper.map(source, destination);
    }


    public <D, T> List<D> mapList(final List<T> entityList, Class<D> outClass) {
        return entityList.stream()
                .map(entity -> map(entity, outClass))
                .collect(Collectors.toList());
    }
}