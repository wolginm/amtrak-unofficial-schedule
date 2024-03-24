package com.markwolgin.amtrak.schedulegenerator.service;

import java.util.List;

import com.markwolgin.amtrak.schedulegenerator.client.AmtrakDataClient;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedResponseObject;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedRoute;
import com.markwolgin.amtrak.schedulegenerator.util.TimetableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleGeneratorService {

    private AmtrakDataClient amtrakDataClient;
    private TimetableUtil timetableUtil;

    @Autowired
    public ScheduleGeneratorService(final AmtrakDataClient amtrakDataClient,
        final TimetableUtil timetableUtil) {

        this.amtrakDataClient = amtrakDataClient;
        this.timetableUtil = timetableUtil;
    }


    protected ConsolidatedResponseObject getRoute(int route) {
        return this.amtrakDataClient.retrieveRoute(route);
    }

    public TimetableFrame getTimetable(int route) {
        ConsolidatedResponseObject consolidatedResponseObject = this.getRoute(route);
        TimetableFrame timetable = null;
        if (consolidatedResponseObject.getRequestedRouteIds().size() != 1)
            log.error("Number of routes found {} != 1", consolidatedResponseObject.getRequestedRouteIds().size());
        else {
            timetable = timetableUtil.buildTimetable(consolidatedResponseObject.getRequestedConsolidatedRoutes()
                    .get().get(Integer.toString(route)));
        }
        return timetable;
    }

    
}
