package com.markwolgin.amtrak.schedulegenerator.view.text;

import com.markwolgin.amtrak.schedulegenerator.model.Timetable;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

public interface IViewSchedule {
    
    String buildSchedule(Timetable timetable);
    String buildWeekdaySchedule(Timetable timetable);
    String buildSaturdaySchedule(Timetable timetable);
    String buildSundaySchedule(Timetable timetable);

    String timetableEntry(TimetableEntry timetableEntry);
}
