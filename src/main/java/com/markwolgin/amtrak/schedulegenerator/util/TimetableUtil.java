package com.markwolgin.amtrak.schedulegenerator.util;

import java.util.*;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedRoute;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
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
        TimetableFrame timetable = null;
        boolean setStationOrder = false;
        String routeName = consolidatedRoute.getRouteLongName();
        List<ConsolidatedTrip> services = new ArrayList<>();

        String defaultFirstStation = consolidatedRoute.getStopOrder().get(0);

        log.info("Begining conversion form ConsolidatedRoute to Timetable for route {}:{}:",
            consolidatedRoute.getRouteId(),
            consolidatedRoute.getRouteShortName(), consolidatedRoute.getRouteLongName());

        return timetable;
    }
}
