package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Skydiver {
    private String name;
    private String licence;
    private List<Jump> jumps;

    // Constructors
    public Skydiver(String name, String licence) {
        this.name = name;
        this.licence = licence;
        this.jumps = new ArrayList<>();
    }

    public Skydiver() {
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public String getLicence() {
        return this.licence;
    }

    public int getPackingtime() {
        if (!this.isLicenced()) {
            return 0;
        }
        return 10;
    }

    public LocalDateTime getEndtime(Flight flight) {
        return flight.getEndtime().plusMinutes(this.getPackingtime());
    }

    /**
     * Returns true if Skydiver is licenced and false otherwise.
     * @return boolean
     */
    public boolean isLicenced() {
        if (this.licence.equals("student")) {
            return false;
        }
        return true;
    }

    // Modifying jumps list
    public void addJump(Jump jump) {
        jump.addPacking(this.getPackingtime());
        this.jumps.add(jump);
    }

    public void removeJump(String id) {
        for (Jump j : jumps) {
            if (j.getID().equals(id)) {
                jumps.remove(j);
                return;
            }
        }
    }

    /**
     * Returns true if a skydiver is available between
     * starttime and endtime,
     * and false otherwise.
     *
     * @param starttime LocalDateTime
     * @param endtime LocalDateTime
     * @return boolean
     */
    public boolean isAvailable(LocalDateTime starttime, LocalDateTime endtime) {
        for (Jump j : this.jumps) {
            if (j.isOverlap(starttime, endtime)) {
                return false;
            }
        }
        return true;
    }
}