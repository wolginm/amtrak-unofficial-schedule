package com.wolginm.amtrak.schedulegenerator.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wolginm.amtrak.data.models.consolidated.Trip;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timetable {
    
    private String routeName;
    private List<String> stations;
    private List<Train> services;

    public LinkedHashMap<String, Train> reorderListToMapForTrain() {
        LinkedHashMap<String, Train> linkedHashMap = new LinkedHashMap<>();

        for (Train train : services) {
            linkedHashMap.put(train.getTrainNumber(), train);
        }

        return linkedHashMap;
    }

}
