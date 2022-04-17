package unsw.skydiving;

import java.time.LocalDateTime;

public class Jump {
    private String id;
    private Flight flight;
    private JumpTime time;

    // Constructor
    public Jump() {
    }

    public Jump(String id, Flight flight) {
        this.id = id;
        this.flight = flight;
    }

    // Getters
    public String getID() {
        return this.id;
    }

    public Flight getFlight() {
        return this.flight;
    }

    public LocalDateTime getStarttime() {
        return this.time.getStart();
    }

    public LocalDateTime getEndtime() {
        return this.time.getEnd();
    }

    /**
     * Returns the index/order of the jump within the flight.
     * @return int
     */
    public int getIndex() {
        return flight.getJumpIndex(this.id);
    }

    public int getDebrieftime() {
        return 0;
    }

    public int getBrieftime() {
        return 0;
    }

    // Setters
    public void setTime(JumpTime time) {
        this.time = time;
    }

    public void addPacking(int time) {
        this.time.addPacking(time);
    }

    /**
     * Returns true if jumptime overlaps with time between
     * starttime and endtime,
     * and false otherwise.
     *
     * @param starttime LocalDateTime
     * @param endtime LocalDateTime
     * @return boolean
     */
    public boolean isOverlap(LocalDateTime starttime, LocalDateTime endtime) {
        return (
            this.time.getStart().equals(starttime) ||
            (this.time.getStart().isBefore(starttime) &&
            this.time.getEnd().isAfter(starttime)) ||
            (this.time.getStart().isBefore(endtime) &&
            this.time.getEnd().isAfter(endtime)) ||
            this.time.getEnd().equals(endtime)
        );
    }
}