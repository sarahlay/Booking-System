package unsw.skydiving;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;

public class Flight implements Comparable <Flight>{
    private String id;
    private Dropzone dropzone;
	private LocalDateTime starttime;
	private LocalDateTime endtime;
	private int load;
	private int maxLoad;
	private int flightNumber;

	// Jumps
	private List<Fun> fun;
	private List<Tandem> tandem;
	private List<Training> training;

    // Constructor
    public Flight (String id, Dropzone dropzone, LocalDateTime starttime,
        LocalDateTime endtime, int maxLoad, int flightNumber) {
		this.id = id;
		this.dropzone = dropzone;
		this.starttime = starttime;
		this.endtime = endtime;
		this.load = 0;
		this.maxLoad = maxLoad;

		this.fun = new ArrayList<>();
		this.tandem = new ArrayList<>();
		this.training = new ArrayList<>();
	}

    // Getters
	public String getFlightID() {
		return this.id;
	}

	public String getDropzone() {
		return this.dropzone.getId();
	}

	public int getVacancies() {
		return this.dropzone.getVacancies();
	}

	public LocalDateTime getStarttime() {
		return this.starttime;
	}

	public int getflightNumber() {
		return this.flightNumber;
	}

	public LocalDateTime getEndtime() {
		return endtime;
	}

	public int getLoad() {
		return load;
	}

	public int getMaxLoad() {
		return maxLoad;
	}

	public int getJumpIndex(String id) {
		if (getFun(id) != null) {
			return fun.indexOf(getFun(id));
		} else if (getTandem(id) != null) {
			return tandem.indexOf(getTandem(id));
		} else {
			return training.indexOf(getTraining(id));
		}
	}

	public Fun getFun(String id) {
		for (Fun f : fun) {
			if (f.getID().equals(id)) return f;
		}
		return null;
	}

	public Tandem getTandem(String id) {
		for (Tandem t : tandem) {
			if (t.getID().equals(id)) return t;
		}
		return null;
	}

	public Training getTraining(String id) {
		for (Training t : training) {
			if (t.getID().equals(id)) return t;
		}
		return null;
	}

	// Setters
	public void setStarttime(LocalDateTime starttime) {
		this.starttime = starttime;
	}

	// Appending to variables
	/**
	 * Adds a fun jump to a list in group size order unless
	 * index is specified.
	 *
	 * @param jump Jump object
	 * @param index int
	 */
	public void addFun(Fun jump, int index) {
		load += jump.getSize();
		this.dropzone.removeVacancies(jump.getSize());

		if (fun.isEmpty()) {
			fun.add(jump);
			return;
		}

		if (index != -1) {
			fun.add(index, jump);
			return;
		}

		for (Fun f : fun) {
			if (jump.getSize() > f.getSize()) {
				fun.add(fun.indexOf(f), jump);
				return;
			} else if (fun.indexOf(f) == fun.size() - 1) {
				fun.add(jump);
				return;
			}
		}
	}

	public void addFun(Fun jump) {
		addFun(jump, -1);
	}

	public void addTandem(Tandem jump, int index) {
		load++;
		this.dropzone.removeVacancies(2);
		tandem.add(index, jump);
	}

	public void addTandem(Tandem jump) {
		addTandem(jump, tandem.size());
	}


	public void addTraining(Training jump, int index) {
		load++;
		this.dropzone.removeVacancies(2);
		training.add(index, jump);
	}

	public void addTraining(Training jump) {
		addTraining(jump, training.size());
	}

	// Removing from variables
	public void removeFun(Fun jump) {
		load -= jump.getSize();
		this.dropzone.addVacancies(jump.getSize());
		fun.remove(jump);
	}

	public void removeTandem(Tandem jump) {
		load--;
		this.dropzone.addVacancies(2);
		tandem.remove(jump);
	}

	public void removeTraining(Training jump) {
		load--;
		this.dropzone.addVacancies(2);
		training.remove(jump);
	}

	public void removeJump(String id) {
		if (getFun(id) != null) {
			removeFun(getFun(id));
			return;
		} else if (getTandem(id) != null) {
			removeTandem(getTandem(id));
			return;
		} else if (getTraining(id) != null) {
			removeTraining(getTraining(id));
			return;
		}
	}

	/**
	 * Returns true if the flight is available for starttime and load.
	 * Returns false otherwise
	 *
	 * @param starttime LocalDateTime
	 * @param load int
	 * @return true if available and false otherwise
	 */
	public boolean isAvailable(LocalDateTime starttime, int load) {
		return (
			// Checking if flight can hold load
			this.getLoad() + load) <= this.getMaxLoad() &&
			// Checking flight time is later than or equal to starttime
			this.getStarttime().compareTo(starttime) >= 0 &&
			// Checking flight is on same day as starttime
			(this.getStarttime().getDayOfYear() == starttime.getDayOfYear()
		);
	}

	public Instructor findInstructor(Skydiver skydiver, LocalDateTime starttime, LocalDateTime endtime, boolean isTandem) {
		return dropzone.findInstructor(skydiver, starttime, endtime, isTandem);
	}

	/**
	 * Generates a jump-run and returns a JSONArray
	 * specifying the order in which jump groups
	 * will exit the plane:
	 * 1. Largest to smallest fun-jump groups (including individuals)
	 * 2. Largest to smallest training-jump groups
	 * 3. Tandem-jumps
	 *
	 */
	public void jumpRun() {
		JSONArray result = new JSONArray();

		// Fun jumps
		for (Fun f : fun) {
			JSONObject obj = new JSONObject();
			obj.put("skydivers", f.getNames());
			result.put(obj);
		}

		// Training jumps
		for (Training t : training) {
			JSONObject obj = new JSONObject();
			obj.put("instructor", t.getInstructorName());
			obj.put("trainee", t.getTraineeName());
			result.put(obj);
		}

		// Tandem jumps
		for (Tandem t : tandem) {
			JSONObject obj = new JSONObject();
			obj.put("passenger", t.getPassengerName());
			obj.put("jump-master", t.getMasterName());
			result.put(obj);
		}
		System.out.println(result.toString());
	}

	@Override
	public int compareTo(Flight flight) {
		return this.starttime.compareTo(flight.starttime);
	}
}