package unsw.skydiving;

import java.time.LocalDateTime;

public class Training extends Jump {
    public Instructor instructor;
    public Skydiver trainee;

    // Constructor
    public Training(String id, LocalDateTime starttime, Flight flight, Instructor instructor, Skydiver trainee) {
		super(id, flight);
        this.instructor = instructor;
		this.trainee = trainee;
		this.setTime(new JumpTime(starttime, flight.getEndtime(), getBrieftime(), getDebrieftime()));
    }

    // Getters and setters
	public String getInstructorName() {
		return instructor.getName();
	}

	public String getTraineeName() {
		return trainee.getName();
	}

	// Methods
	@Override
	public int getDebrieftime() {
		return 15;
	}

}