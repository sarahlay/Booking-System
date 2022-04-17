package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Skydive Booking System for COMP2511.
 *
 * A basic prototype to serve as the "back-end" of a skydive booking system. Input
 * and output is in JSON format.
 *
 * @author Matthew Perry
 *
 */


public class SkydiveBookingSystem {
    private List <Dropzone> dropzones;
    private List <Skydiver> skydivers;
    private List <Jump> jumps;
    private int flightNumber;

    /**
     * Constructs a skydive booking system.
     * Initially, the system contains no flights, skydivers, jumps or dropzones.
     */
    public SkydiveBookingSystem() {
        this.dropzones = new ArrayList<>();
        this.skydivers = new ArrayList<>();
        this.jumps = new ArrayList<>();
        this.flightNumber = 0;
    }

    private void processCommand(JSONObject json) {
        switch (json.getString("command")) {

        case "flight":
            String id = json.getString("id");
            int maxload = json.getInt("maxload");
            LocalDateTime starttime = LocalDateTime.parse(json.getString("starttime"));
            LocalDateTime endtime = LocalDateTime.parse(json.getString("endtime"));
            String dropzoneID = json.getString("dropzone");

            Dropzone dropzone = getDropzone(dropzoneID);
            dropzone.addFlight(new Flight(id, dropzone, starttime, endtime, maxload, flightNumber));
            flightNumber++;

            // Sorting dropzones by most vacancies
            Collections.sort(dropzones, Comparator.comparing(Dropzone::getVacancies).reversed());
            break;

        case "skydiver":
            String name = json.getString("skydiver");
            String licence = json.getString("licence");

            if (licence.equals("instructor") || licence.equals("tandem-master")) {
                id = json.getString("dropzone");
                addInstructor(name, licence, getDropzone(id));
            } else {
                addSkydiver(name, licence);
            }
            break;
        case "cancel":
            id = json.getString("id");
            removeJump(id);
            break;
        case "change":
            changeJump(json);
            break;
        case "request":
            processRequest(json);
            break;
        case "jump-run":
            id = json.getString("id");
            Flight flight = getFlight(id);
            flight.jumpRun();
            break;
        }
    }

    /**
     * Processes a "request".
     *
     * Returns true if successful and false otherwise.
     *
     * @param json JSONObject stipulating details of request
     * @return boolean
     */
    private boolean processRequest(JSONObject json) {
        String type = json.getString("type");
        String id = json.getString("id");
        LocalDateTime starttime = LocalDateTime.parse(json.getString("starttime"));

        switch(type) {
        case "tandem":
            Skydiver passenger = getSkydiver(json.getString("passenger"));
            if (addTandem(id, starttime, passenger)) return true;
            break;
        case "training":
            Skydiver trainee = getSkydiver(json.getString("trainee"));
            if (addTraining(id, starttime, trainee)) return true;
            break;
        case "fun":
            List<Skydiver> divers = new ArrayList<Skydiver>();
            JSONArray array = json.getJSONArray("skydivers");

            // Processing Skydivers
            for (int i = 0; i < array.length(); i++) {
                divers.add(getSkydiver(array.getString(i)));
            }
            if (addFun(id, starttime, divers)) return true;
            break;
        }
        return false;
    }

    /**
     * Changes a jump to a given time if available.
     * Otherwise, system remains as is.
     *
     * @param json JSONObject stipulating details of request
     */
    private void changeJump(JSONObject json) {
        String id = json.getString("id");
        String type = json.getString("type");

        // Store jump
        Jump jump = getJump(id);
        Flight flight = jump.getFlight();
        int index = jump.getIndex();

        // Process request, return if successful
        removeJump(id);
        if (processRequest(json)) return;

        // Restoring jump if unsuccessful
        jumps.add(jump);
        switch(type) {
        case "fun":
            Fun fun = flight.getFun(id);
            flight.addFun(fun, index);
            return;
        case "tandem":
            Tandem tandem = flight.getTandem(id);
            flight.addTandem(tandem, index);
            return;
        case "training":
            Training training = flight.getTraining(id);
            flight.addTraining(training, index);
            return;
        }
    }

    /**
     * Retrieves a Flight object from an id String
     *
     * @param id Flight id
     * @return Flight object
     */
    private Flight getFlight(String id) {
        for (Dropzone d : dropzones) {
            Flight f = d.getFlight(id);
            if (f != null) return f;
        }
        return null;
    }

    /**
     * Retrieves a Jump object from an id String
     *
     * @param id Jump id
     * @return Jump object
     */
    private Jump getJump(String id) {
        for (Jump j : jumps) {
            if (j.getID().equals(id)) return j;
        }
        return null;
    }

    /**
     * Retrieves a Dropzone object from an id String
     *
     * @param id Dropzone id
     * @return Dropzone onject
     */
    private Dropzone getDropzone(String id) {
        for (Dropzone d : dropzones) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        // New dropzone, add to list.
        Dropzone d = new Dropzone(id);
        dropzones.add(d);
        return d;
    }

    /**
     * Retrieves a Skydiver object from a name String
     *
     * @param name Skydiver name
     * @return Skydiver object
     */
    public Skydiver getSkydiver(String name) {
        for (Skydiver s : skydivers) {
            if (s.getName().equals(name)) return s;
        }
        return null;
    }


    /**
     * Adds an Instructor to the system
     *
     * @param name Instructor's name
     * @param licence Instructor's licence
     * @param id Instructor's dropzone id
     */
    private void addInstructor(String name, String licence, Dropzone dropzone) {
        Instructor instructor = new Instructor(name, licence, dropzone);
        skydivers.add(instructor);
        dropzone.addInstructor(instructor);
    }

    /**
     * Adds a Skydiver to the system
     *
     * @param name Skydiver name
     * @param licence Skydiver's licence
     */
    private void addSkydiver(String name, String licence) {
        skydivers.add(new Skydiver(name, licence));
    }

    /**
     * Adds a Tandem Jump to the system.
     *
     * Attempts to find a flight and instructor,
     * If successful, the function will add the Jump to the system,
     * and call outputSuccess().
     * If unsuccessful, the function will call outputReject().
     *
     * @param id Jump id
     * @param starttime Earliest starttime requested
     * @param passenger Passenger of Tandem jump
     * @return true if successful and false otherwise
     */
    private boolean addTandem(String id, LocalDateTime starttime, Skydiver passenger) {
        LocalDateTime startFlight = starttime.plusMinutes(5);
        // Checking Flight Availability
        Flight flight = findFlight(startFlight, 2);
        if (flight != null) {
            Instructor instructor = flight.findInstructor(passenger, starttime, flight.getEndtime().plusMinutes(10), true);
            if (instructor != null &&
                passenger.isAvailable(starttime, passenger.getEndtime(flight))
            ) {
                Tandem jump = new Tandem(id, flight, instructor, passenger);
                passenger.addJump(jump);
                instructor.addJump(jump);
                flight.addTandem(jump);
                jumps.add(jump);
                outputSuccess(flight);
                return true;
            }
        }
        outputReject();
        return false;
    }

    /**
     * Adds a Training Jump to the system.
     *
     * Attempts to find a flight and instructor,
     * If successful, the function will add the Jump to the system,
     * and call outputSuccess().
     * If unsuccessful, the function will call outputReject().
     *
     * @param id Jump id
     * @param starttime Earliest starttime requested
     * @param trainee Trainee of Training jump
     * @return true if successful and false otherwise
     */
    private boolean addTraining(String id, LocalDateTime starttime, Skydiver trainee) {
        // Checking Flight Availability
        Flight flight = findFlight(starttime, 2);
        if (flight != null) {
            Instructor instructor = flight.findInstructor(trainee, starttime, flight.getEndtime().plusMinutes(25), false);
            if (instructor != null &&
                trainee.isAvailable(starttime, trainee.getEndtime(flight).plusMinutes(15))
            ) {
                // Check trainee availability
                Training jump = new Training(id, starttime, flight, instructor, trainee);
                trainee.addJump(jump);
                instructor.addJump(jump);
                flight.addTraining(jump);
                jumps.add(jump);
                outputSuccess(flight);
                return true;
            }
        }
        outputReject();
        return false;
    }

    /**
     * Adds a Fun Jump to the system.
     *
     * Attempts to find a flight and calls verifyDivers() to check licence & availability,
     * If successful, the function will add the Jump to the system,
     * and call outputSuccess().
     * If unsuccessful, the function will call outputReject().
     *
     * @param id Jump id
     * @param starttime Earliest starttime requested
     * @param divers List of skydivers to attend jump
     * @return true if successful and false otherwise
     */
    private boolean addFun(String id, LocalDateTime starttime, List<Skydiver> divers) {
        // Checking Flight & Instructor Availability
        Flight flight = findFlight(starttime, divers.size());
        if (flight != null && verifyDivers(divers, starttime, flight.getEndtime())) {
            Fun jump = new Fun(id, starttime, flight, divers);
            for (Skydiver s : divers) {
                s.addJump(jump);
            }
            flight.addFun(jump);
            jumps.add(jump);
            outputSuccess(flight);
            return true;
        }
        outputReject();
        return false;
    }

    /**
     * Verifies that all divers in list are licenced and available between starttime & endtime.
     *
     * @param divers List of Skydiver objects
     * @param starttime Start time
     * @param endtime End time
     * @return true if verified and false otherwise
     */
    private boolean verifyDivers(List<Skydiver> divers, LocalDateTime starttime, LocalDateTime endtime) {
        for (Skydiver s : divers) {
            if (!s.isLicenced() || !s.isAvailable(starttime, endtime.plusMinutes(10))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a flight closest to starttime that hold a specific load.
     *
     * The flight being returned is:
     *  - The earliest Flight
     *  - A flight with most vacancies in dropzone
     *      (if multiple flights contain the same time time)
     *  - A flight with the earliest index
     *      (if multiple dropzones contain the same vacancies)
     *
     * @param starttime Earliest start time
     * @param load Number of skydivers (int)
     * @return Flight object if found and null otherwise
     */
    private Flight findFlight(LocalDateTime starttime, int load) {
        List <Flight> flights = new ArrayList<>();
        for (Dropzone d : dropzones) {
            // Finding flights in dropzones with most to least vacancies
            Flight f = d.findFlight(starttime, load);
            if (f != null) {
                if (flights.isEmpty()) {
                    flights.add(f);
                } else if (
                    flights.get(0).getStarttime().compareTo(f.getStarttime()) > 0 ||
                    (flights.get(0).getStarttime().compareTo(f.getStarttime()) == 0 &&
                    flights.get(0).getVacancies() < f.getVacancies())
                ){
                    // f is earliest flight or one with most vacancies
                    // Adds f to list and removes existing
                    flights.remove(0);
                    flights.add(0, f);
                }
            }
        }

        // If multiple dropzones, choose earliest flight logged
        Collections.sort(flights, Comparator.comparing(Flight::getflightNumber));
        if (!flights.isEmpty()) return flights.get(0);
        return null;
    }

    /**
     * Removes a jump with id from system.
     *
     * @param id Jump id
     */
    private void removeJump(String id) {
        // Remove existing bookings/jumps
        for (Jump j : jumps) {
            if (j.getID().equals(id)) {
                jumps.remove(j);
                break;
            }
        }
        for (Skydiver s : skydivers) {
            s.removeJump(id);
        }
        for (Dropzone d : dropzones) {
            d.removeJump(id);
        }
    }

    /**
     * Outputs a successful request to standard output.
     *
     * @param flight Flight object
     */
    private void outputSuccess(Flight flight) {
        JSONObject result = new JSONObject();
        result.put("flight", flight.getFlightID());
        result.put("dropzone", flight.getDropzone());
        result.put("status", "success");
        System.out.println(result.toString(2));
    }

    /**
     * Outputs an unsuccessful request to standard output.
     */
    private void outputReject() {
        JSONObject result = new JSONObject();
        result.put("status", "rejected");
        System.out.println(result.toString(2));
    }

    public static void main(String[] args) {
        SkydiveBookingSystem system = new SkydiveBookingSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }
}
