package seedu.address.testutil.scheduleutil;

import java.time.LocalDateTime;

import seedu.address.model.person.schedule.Timeslot;
import seedu.address.model.person.schedule.Venue;

/**
 * Typical Timeslots.
 */
public class TypicalTimeslots {

    public static final String STARTTIMETEXT1 = "03122007:1030-";
    public static final String ENDTIMETEXT1 = "03122007:1130-";

    public static final LocalDateTime START_TIME1 = LocalDateTime.parse("2007-12-03T10:30:00");
    public static final LocalDateTime END_TIME1 = LocalDateTime.parse("2007-12-03T11:30:00");
    public static final Venue VENUE1 = new Venue("venue1");

    public static final Timeslot TIME_SLOT1 = new Timeslot(
            START_TIME1, END_TIME1, VENUE1
    );

    public static final LocalDateTime START_TIME2 = LocalDateTime.parse("2007-12-03T12:30:00");
    public static final LocalDateTime END_TIME2 = LocalDateTime.parse("2007-12-03T13:30:00");
    public static final Venue VENUE2 = new Venue("venue2");

    public static final Timeslot TIME_SLOT2 = new Timeslot(
            START_TIME2, END_TIME2, VENUE2
    );

    public static final LocalDateTime START_TIME3 = LocalDateTime.parse("2007-12-03T14:30:00");
    public static final LocalDateTime END_TIME3 = LocalDateTime.parse("2007-12-03T15:30:00");
    public static final Venue VENUE3 = new Venue("venue3");

    public static final Timeslot TIME_SLOT3 = new Timeslot(
            START_TIME3, END_TIME3, VENUE3
    );
}
