package com.markwolgin.amtrak.schedulegenerator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
public class ObjectsUtil {

    private final ObjectMapper objectMapper;

    public ObjectsUtil(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JsonNullableModule());
    }

    public <T> T loadObject(final InputStream inputStream, final Class<T> tClass) throws IOException {
        return objectMapper.readValue(inputStream, tClass);
    }

    public <T> T loadObject(final String inputStream, final Class<T> tClass) throws IOException {
        return objectMapper.readValue(inputStream, tClass);
    }

    public <T> T mapToObject(final Map<String, String> mapOfElements, final Class<T> tClass) {
        return objectMapper.convertValue(mapOfElements, tClass);
    }

}
