package unsw.skydiving;

import java.util.List;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.time.LocalDateTime;

public class Fun extends Jump {
    private List<Skydiver> skydivers;
    private int size;

    // Constructor
    public Fun(String id, LocalDateTime starttime, Flight flight, List<Skydiver> skydivers) {
        super(id, flight);
        this.size = skydivers.size();
        this.skydivers = new ArrayList<>(skydivers);
		this.setTime(new JumpTime(starttime, flight.getEndtime(), getBrieftime(), getDebrieftime()));
    }

    // Getters
    public int getSize() {
        return this.size;
    }

    public List<Skydiver> getSkydivers() {
        return this.skydivers;
    }

    /**
     * @return JSONArray containing skydivers' names in alphabetical order
     */
    public JSONArray getNames() {
        Collections.sort(skydivers, Comparator.comparing(Skydiver::getName));
        JSONArray names = new JSONArray();
        for (Skydiver s : skydivers) {
            names.put(s.getName());
        }
        return names;
    }

}