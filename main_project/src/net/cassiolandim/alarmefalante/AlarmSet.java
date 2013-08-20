package net.cassiolandim.alarmefalante;

import java.io.Serializable;

public class AlarmSet implements Serializable {

	private static final long serialVersionUID = 1L;

	Long id;
	String name = "Alarme";
	Boolean enabled = true;
	Boolean vibration = true;
	Integer volume = 50;
	Integer hour = 7;
	Integer minute = 0;
	Integer snoozetime = 10;
	String weekdays = "1,2,3,4,5,6,7";
	
	@Override
	public String toString() {
		return name;
	}
}
