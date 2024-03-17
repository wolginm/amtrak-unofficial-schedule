package com.markwolgin.amtrak.schedulegenerator.service;

import java.util.List;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedRoute;
import com.markwolgin.amtrak.schedulegenerator.util.TimetableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleGeneratorService {


    private TimetableUtil timetableUtil;

    @Autowired
    public ScheduleGeneratorService(
        TimetableUtil timetableUtil) {

        this.timetableUtil = timetableUtil;
    }


    public List<ConsolidatedRoute> getRoute(int route) {
        return List.of();
    }

    public TimetableFrame getTimetable(int route) {
        List<ConsolidatedRoute> routes = this.getRoute(route);
        TimetableFrame timetable = null;
        if (routes.size() != 1) log.error("Number of routes found {} != 1", routes.size());
        else {
            timetable = this.timetableUtil.buildTimetable(routes.get(0));
        }
        return timetable;
    }

    
}
