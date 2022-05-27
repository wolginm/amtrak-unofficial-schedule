package com.wolginm.amtrak.schedulegenerator.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wolginm.amtrak.data.models.consolidated.ConsolidatedRoute;
import com.wolginm.amtrak.data.service.AmtrakDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleGeneratorService {

    private AmtrakDataService amtrakDataService;

    @Autowired
    public ScheduleGeneratorService(AmtrakDataService amtrakDataService) {
        this.amtrakDataService = amtrakDataService;
    }


    public List<ConsolidatedRoute> getRoute(int route) {
        return this.amtrakDataService
            .getRoutes()
            .stream()
            .filter((selected) -> selected.getRoute().getRoute_id() == route)
            .collect(Collectors.toList());
    }
}
