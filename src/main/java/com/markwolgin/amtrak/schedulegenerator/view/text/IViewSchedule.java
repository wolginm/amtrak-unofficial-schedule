package com.markwolgin.amtrak.schedulegenerator.view.text;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

import java.util.Date;

/**
 * A common set of methods that can be used to visually construct a schedule.
 * The way to access the data is via {@link #buildSchedule()}.
 */
public interface IViewSchedule {

    /**
     * Builds the general schedule, Weekday, Saturday, and Sunday; in both directions.
     * @param timetable {@link TimetableFrame} holding all {@link TimetableEntry}s.
     * @return          The composite schedule.
     */
    String buildSchedule(TimetableFrame timetable);

    String buildSchedule(TimetableFrame timetableFrame, Date startDate, Date endDate);

    String buildWeekdaySchedule(TimetableFrame timetable, Boolean direction);

    String buildSaturdaySchedule(TimetableFrame timetable, Boolean direction);
    String buildSundaySchedule(TimetableFrame timetable, Boolean direction);

    String timetableEntry(TimetableEntry timetableEntry);
}
