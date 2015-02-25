package pt.lsts.accl.event;

import java.util.Date;

public abstract class AbstractACCLEvent {

	private double timestamp = System.currentTimeMillis() / 1000.0;
	
	public double getAge() {
		return (System.currentTimeMillis() / 1000.0) - timestamp;
	}
	
	public double getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return "["+new Date((long)(timestamp * 1000))+"] "+getClass().getSimpleName();
	}
}
