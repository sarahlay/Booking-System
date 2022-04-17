package unsw.skydiving;

import java.time.LocalDateTime;

public class Instructor extends Skydiver {
    private Dropzone dropzone;
    private boolean isTandemMaster;

    // Constructor
    public Instructor(String name, String licence, Dropzone dropzone) {
        super(name, licence);
        this.dropzone = dropzone;
        if (this.getLicence().equals("tandem-master")) {
            this.isTandemMaster = true;
        }
    }

    // Getters
    public Dropzone getDropzone() {
        return this.dropzone;
    }

    /**
     * @return boolean
     */
    public boolean isTandemMaster() {
        return this.isTandemMaster;
    }

    /**
     * Returns true if an instructor:
     * - is available between flight times
     * - is not the skydiver
     * - is based in the same dropzone
     *
    */
    public boolean isAvailable(Skydiver skydiver, LocalDateTime starttime, LocalDateTime endtime, Dropzone dzone) {
        return (
            // Checking if instructor is available
            this.isAvailable(starttime, endtime) &&
            // Checking that instructor is not the skydiver
            !this.getName().equals(skydiver.getName()) &&
            // Checking that the dropzone correlates
            dropzone.equals(dzone)
        );
    }

    /**
     * Returns true if an instructor:
     * - is a Tandem Master
     * - is available 5 minutes prior to flight
     * - is available between flight times
     * - is not the skydiver
     * - is based in the same dropzone
     *
     * @param skydiver Skydiver object
     * @param flight Flight object
     * @return boolean
     */
    public boolean isTandemAvailable(Skydiver skydiver, LocalDateTime starttime, LocalDateTime endtime, Dropzone dropzone) {
        return (
            // Checking licence
            isTandemMaster() &&
            // Checking instructor is available during briefing
            this.isAvailable(starttime, endtime) &&
            isAvailable(skydiver, starttime, endtime, dropzone)
        );
    }
}
