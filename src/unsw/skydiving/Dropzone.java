package unsw.skydiving;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;

public class Dropzone {
    private String id;
    private List <Flight> flights;
    private List <Instructor> instructors;
    private int vacancies;

    // Constructor
    public Dropzone(String id) {
        this.id = id;
        this.flights = new ArrayList<>();
        this.instructors = new ArrayList<>();
        this.vacancies = 0;
    }

    // Getters
    public String getId() {
        return this.id;
    }

    public int getVacancies() {
        return this.vacancies;
    }

    public Flight getFlight(String id) {
        for (Flight f : flights) {
            if (f.getFlightID().equals(id)) {
                return f;
            }
        }
        return null;
    }

    public Fun getFun(String id) {
		for (Flight f : flights) {
            Fun jump = f.getFun(id);
            if (jump != null) return jump;
        }
        return null;
	}

	public Tandem getTandem(String id) {
		for (Flight f : flights) {
            Tandem jump = f.getTandem(id);
            if (jump != null) return jump;
        }
        return null;
	}

	public Training getTraining(String id) {
		for (Flight f : flights) {
            Training jump = f.getTraining(id);
            if (jump != null) return jump;
        }
        return null;
    }

    // Appending to variables
    public void addVacancies(int spots) {
        vacancies += spots;
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
        Collections.sort(flights);
        vacancies += flight.getMaxLoad();
    }

    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
    }

    // Removing from variables
    public void removeVacancies(int spots) {
        vacancies -= spots;
    }

    public void removeJump(String id) {
        for (Flight f : flights) {
            f.removeJump(id);
        }
    }

    /**
     * Returns the earliest flight available for a given load.
     *
     * @param starttime Flight start time
     * @param load Amount of skydivers
     * @return Flight object
     */
    public Flight findFlight(LocalDateTime starttime, int load) {
        for (Flight f : flights) {
            if (f.isAvailable(starttime, load)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns an Instructor available for the given flight.
     * Returns a Tandem Master if variable isTandem is set to true.
     *
     * @param skydiver Skydiver object (passenger or trainee)
     * @param starttime LocalDateTime
     * @param endtime LocalDateTime
     * @param isTandem boolean - will search for Tandem Master
     * @return Instructor object
     */
    public Instructor findInstructor(Skydiver skydiver, LocalDateTime starttime, LocalDateTime endtime, boolean isTandem) {
        for (Instructor i : instructors) {
            if (isTandem && i.isTandemAvailable(skydiver, starttime, endtime, this)) {
                return i;
            } else if (i.isAvailable(skydiver, starttime, endtime, this)) {
                return i;
            }
        }
        return null;
    }

}
