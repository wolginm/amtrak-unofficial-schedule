package com.wolginm.amtrak.schedulegenerator.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        Timetable timetable;
        boolean setStationOrder = false;
        String routeName = consolidatedRoute.getRoute().getRoute_long_name();
        List<Train> services = new ArrayList<>();
        
        Map<String, Map<Integer, Integer>> stations = new HashMap<>();
            consolidatedRoute.getStopIds().forEach(entry -> stations.put(entry, new HashMap<>()));        
        String defaultFirstStation = consolidatedRoute.getTrips().get(0).getSchedule().next().getStop().getStop_id();

        log.info("Begining conversion form ConsolidatedRoute to Timetable for route {}:{}", 
            consolidatedRoute.getRoute().getRoute_id(), 
            consolidatedRoute.getRoute().getRoute_long_name());
        for (Trip trip : consolidatedRoute.getTrips()) {

            services.add(this.generateTrainFromTrip(trip, defaultFirstStation));
            
            if (!setStationOrder ) {
                Iterator<Stop> iterator = trip.getSchedule();
                Stop stop = null;
                int count = 0;
                while (iterator.hasNext()) {
                    stop = iterator.next();
                    count ++;
                }
                if (count == consolidatedRoute.getMaxNumberOfStationPerDirection().get(trip.getDirectionId())) {
                    iterator = trip.getSchedule();
                    stop = null;
                    while (iterator.hasNext()) {
                        stop = iterator.next();
                        stations.get(stop.getStop().getStop_id())
                            .put(trip.getDirectionId(), 
                                stop.getStopTimes().getStop_sequence());
                        // stations.replace(stop.getStop().getStop_id(), 
                        //     stop.getStopTimes().getStop_sequence());
                    }
                }
            }
        }

        timetable = new Timetable();
        timetable.setStartDate(consolidatedRoute.getTrips().get(0).getServiceDetails().getStartDate());
        timetable.setEndDate(consolidatedRoute.getTrips().get(0).getServiceDetails().getEndDate());
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
        service.setWeekday(trip.getServiceDetails().isWeekday());
        service.setSaturday(trip.getServiceDetails().isSaturday());
        service.setSunday(trip.getServiceDetails().isSunday());
        service.setDirection(trip.getDirectionId());
        service.setDepartureTime(departureTime);
        service.setTrainNumber(trip.getTripId());
        service.setSchedule(timetableEntries);
        service.setDayScheduleStarts(trip.getServiceDetails().getStartDate());
        service.setDayScheduleEnds(trip.getServiceDetails().getEndDate());

        return service;
    }
}
