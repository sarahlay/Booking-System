package unsw.skydiving;

public class Tandem extends Jump {
    private Instructor tandemMaster;
	private Skydiver passenger;

    // Constructor
    public Tandem(String id, Flight flight, Instructor tandemMaster, Skydiver passenger) {
		super(id, flight);
        this.tandemMaster = tandemMaster;
		this.passenger = passenger;
		this.setTime(new JumpTime(flight.getStarttime(), flight.getEndtime(), getBrieftime(), getDebrieftime()));
	}

    // Getters
	public Instructor getTandemMaster() {
		return this.tandemMaster;
	}

	public Skydiver getPassenger() {
		return this.passenger;
	}

	public String getPassengerName() {
		return passenger.getName();
	}

	public String getMasterName() {
		return tandemMaster.getName();
	}

	@Override
	public int getBrieftime() {
		return 5;
	}

}