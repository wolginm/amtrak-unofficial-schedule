package com.markwolgin.amtrak.schedulegenerator.util;

import java.time.LocalDate;
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

    private Map<Integer, TimetableFrame> timetableCache;
    private final OperatingPattern WEEKDAY = new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(false).sunday(false);
    private final OperatingPattern WEEKEND = new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(false).saturday(true).sunday(true);
    private final OperatingPattern SATURDAY = new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(false).saturday(true).sunday(false);
    private final OperatingPattern SUNDAY = new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(false).saturday(false).sunday(true);
    private final OperatingPattern ALL_WEEK = new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(true).sunday(true);


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
        for (ConsolidatedTrip trip : consolidatedRoute.getTripList().get().values()) {
            log.debug("\t{}:{}\t{}", trip.getTripEffectiveOnDate(), trip.getTripNoLongerEffectiveOnDate().plusDays(6), (trip.getTripEffectiveOnDate().isBefore(LocalDate.now()) &&
                    trip.getTripNoLongerEffectiveOnDate().plusDays(6).isAfter(LocalDate.now())) || (trip.getTripEffectiveOnDate().isEqual(LocalDate.now()) || trip.getTripNoLongerEffectiveOnDate().isEqual(LocalDate.now())));
        }

        Map<Boolean, TimetableEntry> weekday = new HashMap<>(2);
        Map<Boolean, TimetableEntry> saturday = new HashMap<>(2);
        Map<Boolean, TimetableEntry> sunday = new HashMap<>(2);


        weekday.put(false, this.buildTimetableEntry(consolidatedRoute, WEEKDAY, false));
        weekday.put(true, this.buildTimetableEntry(consolidatedRoute, WEEKDAY, true));

        saturday.put(false, this.buildTimetableEntry(consolidatedRoute, SATURDAY, false));
        saturday.put(true, this.buildTimetableEntry(consolidatedRoute, SATURDAY, true));

        sunday.put(false, this.buildTimetableEntry(consolidatedRoute, SUNDAY, false));
        sunday.put(true, this.buildTimetableEntry(consolidatedRoute, SUNDAY, true));
        TimetableFrame timetable = new TimetableFrame(consolidatedRoute.getRouteLongName(), consolidatedRoute.getRouteId(),
                weekday, saturday, sunday, consolidatedRoute.getStopOrder(), consolidatedRoute.getAllStops().get());

        String defaultFirstStation = consolidatedRoute.getStopOrder().get(0);

        log.info("Beginning conversion form ConsolidatedRoute to Timetable for route {}:{}:{}",
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
            return (entry.getValue().getOperatingPattern().equals(matchingPattern)) &&
                    ((entry.getValue().getDirectionId() == 1) == direction) &&
                    ((entry.getValue().getTripEffectiveOnDate().isBefore(LocalDate.now()) &&
                            entry.getValue().getTripNoLongerEffectiveOnDate().plusDays(6).isAfter(LocalDate.now())) ||
                                (entry.getValue().getTripEffectiveOnDate().isEqual(LocalDate.now()) ||
                                        entry.getValue().getTripNoLongerEffectiveOnDate().isEqual(LocalDate.now())));
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                consolidatedRoute.getStopOrder(),
                direction,
                consolidatedRoute.getIndexDirZero(),
                consolidatedRoute.getIndexDirOne());
    }
}
