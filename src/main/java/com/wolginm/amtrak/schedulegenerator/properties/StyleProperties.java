package com.wolginm.amtrak.schedulegenerator.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("schedule")
public class StyleProperties {
    
    @Value("schedule.general.font.family")
    private String generalFontFamily;

    @Value("schedule.general.font.primary-color")
    private String generalFontPrimaryColor;

    @Value("schedule.general.font.secondary-color")
    private String generalFontSecondaryColor;

    @Value("schedule.header.font.primary-color")
    private String headerFontPrimaryColor;

    @Value("schedule.header.font.background-color")
    private String headerFontBackgroundColor;

    @Value("schedule.direction.font.font-size")
    private String directionFontSize;

    @Value("schedule.direction.font.primary-color")
    private String directionFontPrimaryColor;

    @Value("schedule.timetable.time.font.family")
    private String timetableTimeFontSize;

    @Value("schedule.timetable.time.font.primary-color")
    private String timetableTimeFontPrimaryColor;

    @Value("schedule.timetable.station-major.font.family")
    private String timetableStationMajorFontSize;

    @Value("schedule.timetable.station-major.font.primary-color")
    private String timetableStationMajorFontPrimaryColor;

    @Value("schedule.timetable.station-minor.font.family")
    private String timetableStationMinorFontSize;

    @Value("schedule.timetable.station-minor.font.primary-color")
    private String timetableStationMinorFontPrimaryColor;

    @Value("schedule.timetable.station-alt.font.family")
    private String timetableStationAltFontSize;

    @Value("schedule.timetable.station-alt.font.primary-color")
    private String timetableStationAltFontPrimaryColor;
    
}
