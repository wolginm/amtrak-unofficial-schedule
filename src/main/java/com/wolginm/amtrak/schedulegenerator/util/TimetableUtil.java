package com.wolginm.amtrak.schedulegenerator.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.models.consolidated.Stop;
import com.wolginm.amtrak.data.models.consolidated.Trip;
import com.wolginm.amtrak.schedulegenerator.model.Timetable;
import com.wolginm.amtrak.schedulegenerator.model.TimetableEntry;
import com.wolginm.amtrak.schedulegenerator.model.Train;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TimetableUtil {

    Map<Integer, Timetable> timetableCache;

    @Autowired
    public TimetableUtil() {
        this.timetableCache = new HashMap<>();
    }

    public Timetable buildTimetable(ConsolidatedRoute consolidatedRoute) {
        Timetable timetable = null;
        if (!this.timetableCache.containsKey(consolidatedRoute.getRoute().getRoute_id())) {
            this.timetableCache.put(consolidatedRoute.getRoute().getRoute_id(),
                this.generateTimetableFromRoute(consolidatedRoute));
        }

        timetable = this.timetableCache.get(consolidatedRoute.getRoute().getRoute_id());
        return timetable;
    }

    private Timetable generateTimetableFromRoute(ConsolidatedRoute consolidatedRoute) {
        String routeName = consolidatedRoute.getRoute().getRoute_long_name();
        List<String> stations = consolidatedRoute.getAllStopIds();
        List<Train> services = new ArrayList<>();
        Timetable timetable;
        String defaultFirstStation = consolidatedRoute.getTrips().get(0).getSchedule().next().getStop().getStop_id();

        for (Trip trip : consolidatedRoute.getTrips()) {
            services.add(this.generateTrainFromTrip(trip, defaultFirstStation));
        }

        timetable = new Timetable();
        timetable.setRouteName(routeName);
        timetable.setServices(services);
        timetable.setStations(stations);

        return timetable;
    }

    private Train generateTrainFromTrip(Trip trip, String defaultStation) {
        Train service;
        Iterator<Stop> stopIterator = trip.getSchedule();
        LocalTime departureTime = LocalTime.MIN;
        Map<String, TimetableEntry> timetableEntries = new LinkedHashMap<>();
        Stop currentStop;
        String defaultStartStation = null;

        while (stopIterator.hasNext()) {
            currentStop = stopIterator.next();

            if (defaultStartStation == null) defaultStartStation = currentStop.getStop().getStop_id();

            if (departureTime.compareTo(currentStop.getStopTimes().getDeparture_time()) < 0) {
                departureTime = currentStop.getStopTimes().getDeparture_time();
            }
            timetableEntries.put(currentStop.getStop().getStop_id(),
                new TimetableEntry(currentStop.getStopTimes().getArrival_time(),
                    currentStop.getStopTimes().getDeparture_time(), currentStop.getStop().getStop_id(),
                    currentStop.getStop().getStop_name(),
                    currentStop.getStopTimes().getStop_sequence()));
        }   

        service = new Train();
        service.setDirection(defaultStartStation.equalsIgnoreCase(defaultStation));
        service.setDepartureTime(departureTime);
        service.setTrainNumber(trip.getTripId());
        service.setSchedule(timetableEntries);

        return service;
    }
}
