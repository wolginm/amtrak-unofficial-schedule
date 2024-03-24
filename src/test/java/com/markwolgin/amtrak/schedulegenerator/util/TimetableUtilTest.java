package com.markwolgin.amtrak.schedulegenerator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedResponseObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.LinkPermission;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimetableUtilTest {

    private final ClassLoader classLoader = TimetableUtilTest.class.getClassLoader();
    private final ConsolidatedResponseObject consolidatedResponseObject = loadData(classLoader.getResourceAsStream("data_response.json"));

    private ConsolidatedResponseObject loadData(InputStream resourceAsStream) {
        try {
            return new ObjectsUtil(new ObjectMapper().registerModule(new JavaTimeModule())).loadObject(resourceAsStream, ConsolidatedResponseObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @InjectMocks
    private TimetableUtil timetableUtil;

    @Test
    void testBuildTimetable() {
        TimetableFrame timetableFrame = this.timetableUtil.buildTimetable(this.consolidatedResponseObject.getRequestedConsolidatedRoutes().get().get("94"));
    }

}