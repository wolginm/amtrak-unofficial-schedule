package com.wolginm.amtrak.schedulegenerator.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.service.AmtrakDataService;
import com.wolginm.amtrak.schedulegenerator.model.Timetable;
import com.wolginm.amtrak.schedulegenerator.util.TimetableUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleGeneratorService {

    private AmtrakDataService amtrakDataService;
    private TimetableUtil timetableUtil;

    @Autowired
    public ScheduleGeneratorService(AmtrakDataService amtrakDataService,
        TimetableUtil timetableUtil) {
        this.amtrakDataService = amtrakDataService;
        this.timetableUtil = timetableUtil;
    }


    public List<ConsolidatedRoute> getRoute(int route) {
        return this.amtrakDataService
            .getRoutes()
            .stream()
            .filter((selected) -> selected.getRoute().getRoute_id() == route)
            .collect(Collectors.toList());
    }

    public Timetable getTimetable(int route) {
        List<ConsolidatedRoute> routes = this.getRoute(route);
        Timetable timetable = null;
        if (routes.size() != 1) log.error("Number of routes found {} != 1", routes.size());
        else {
            timetable = this.timetableUtil.buildTimetable(routes.get(0));
        }
        return timetable;
    }

    
}
