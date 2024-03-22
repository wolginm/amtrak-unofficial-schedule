package com.markwolgin.amtrak.schedulegenerator.util;

import java.util.*;
import java.util.stream.Collectors;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedRoute;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimetableUtil {

    Map<Integer, TimetableFrame> timetableCache;

    @Autowired
    public TimetableUtil() {
        this.timetableCache = new HashMap<>();
    }

    public TimetableFrame buildTimetable(ConsolidatedRoute consolidatedRoute) {
        TimetableFrame timetable;
        if (!this.timetableCache.containsKey(Integer.valueOf(consolidatedRoute.getRouteId()))) {
            this.timetableCache.put(Integer.valueOf(consolidatedRoute.getRouteId()),
                this.generateTimetableFromRoute(consolidatedRoute));
        }

        timetable = this.timetableCache.get(Integer.valueOf(consolidatedRoute.getRouteId()));
        return timetable;
    }

    private TimetableFrame generateTimetableFromRoute(ConsolidatedRoute consolidatedRoute) {
        Map<Boolean, TimetableEntry> weekday = new HashMap<>(2);
        Map<Boolean, TimetableEntry> saturday = new HashMap<>(2);
        Map<Boolean, TimetableEntry> sunday = new HashMap<>(2);

        TimetableFrame timetable = new TimetableFrame(consolidatedRoute.getRouteShortName(), consolidatedRoute.getRouteId(),
                weekday, saturday, sunday, consolidatedRoute.getStopOrder(), consolidatedRoute.getAllStops().get());

        String defaultFirstStation = consolidatedRoute.getStopOrder().get(0);

        log.info("Begining conversion form ConsolidatedRoute to Timetable for route {}:{}:",
            consolidatedRoute.getRouteId(),
            consolidatedRoute.getRouteShortName(), consolidatedRoute.getRouteLongName());

        return timetable;
    }

    private TimetableEntry buildTimetableEntry(final ConsolidatedRoute consolidatedRoute, final OperatingPattern matchingPattern, final boolean direction) {
        return new TimetableEntry(consolidatedRoute
                .getTripList()
                .get()
                .entrySet()
                .stream()
                .filter(entry -> {
            return (entry.getValue().getOperatingPattern().getMonday().equals(matchingPattern.getMonday()) &&
                    entry.getValue().getOperatingPattern().getTuesday().equals(matchingPattern.getTuesday()) &&
                    entry.getValue().getOperatingPattern().getWednesday().equals(matchingPattern.getWednesday()) &&
                    entry.getValue().getOperatingPattern().getThursday().equals(matchingPattern.getThursday()) &&
                    entry.getValue().getOperatingPattern().getFriday().equals(matchingPattern.getFriday()) &&
                    entry.getValue().getOperatingPattern().getSaturday().equals(matchingPattern.getSaturday()) &&
                    entry.getValue().getOperatingPattern().getSunday().equals(matchingPattern.getSunday()) &&
                    ((entry.getValue().getDirectionId() == 1) == direction));
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                consolidatedRoute.getStopOrder(),
                direction);
    }
}
