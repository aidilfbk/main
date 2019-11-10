package seedu.address.model.display.timeslots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.display.locationdata.ClosestCommonLocationData;

class FreeTimeslotTest {

    private FreeTimeslot freeTimeslot;

    private int id;
    private ArrayList<String> venues;
    private ClosestCommonLocationData closestCommonLocationData;
    private LocalTime startTime;
    private LocalTime endTime;

    @BeforeEach
    void init() {

        id = 1;
        venues = new ArrayList<>(List.of("venue1", "venue2"));
        closestCommonLocationData = new ClosestCommonLocationData();
        closestCommonLocationData.setErrorResponse("error");
        startTime = LocalTime.of(10, 30);
        endTime = LocalTime.of(11, 30);

        freeTimeslot = new FreeTimeslot(
                id,
                venues,
                closestCommonLocationData,
                startTime,
                endTime
        );
    }

    @Test
    void testToString() {
        assertNotNull(freeTimeslot.toString());
    }

    @Test
    void getId() {
        assertEquals(id, freeTimeslot.getId());
    }

    @Test
    void getClosestCommonLocationData() {
        assertEquals(freeTimeslot.getClosestCommonLocationData().toString(),
                closestCommonLocationData.toString());
    }

    @Test
    void getVenues() {
        freeTimeslot.setVenues(venues);
        assertEquals(venues, freeTimeslot.getVenues());
    }

    @Test
    void getStartTime() {
        assertEquals(LocalTime.of(10, 30), freeTimeslot.getStartTime());
    }

    @Test
    void setStartTime() {
        freeTimeslot.setStartTime(LocalTime.of(11, 0));
        assertEquals(LocalTime.of(11, 0), freeTimeslot.getStartTime());

    }

    @Test
    void getEndTime() {
        assertEquals(LocalTime.of(11, 30), freeTimeslot.getEndTime());
    }

    @Test
    void setEndTime() {
        freeTimeslot.setEndTime(LocalTime.of(12, 0));
        assertEquals(LocalTime.of(12, 0), freeTimeslot.getEndTime());
    }
}