package unsw.skydiving;

import java.time.LocalDateTime;

public class JumpTime {
	private LocalDateTime start;
	private LocalDateTime end;

	// Constructor
	public JumpTime(LocalDateTime start, LocalDateTime end, int brief, int debrief) {
		this.start = start.minusMinutes(brief);
		this.end = end.plusMinutes(debrief);
	}

	// Getters
	public LocalDateTime getStart() {
		return this.start;
	}

	public LocalDateTime getEnd() {
		return this.end;
	}

	// Setters
	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	// Adds packing time to end time
	public void addPacking(int time) {
		this.end = end.plusMinutes(time);
	}
}