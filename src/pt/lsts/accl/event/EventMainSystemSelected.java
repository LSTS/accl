package pt.lsts.accl.event;

public class EventMainSystemSelected extends AbstractACCLEvent {

	private String system;
	
	public EventMainSystemSelected(String system) {
		this.system = system;
	}

	/**
	 * @return the system
	 */
	public final String getSystem() {
		return system;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - "+system;
	}	
}
