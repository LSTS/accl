package pt.lsts.accl.event;

public class EventSystemConnected extends AbstractACCLEvent {

	private String sysName;
		
	public EventSystemConnected(String systemName) {
		this.sysName = systemName;
	}

	/**
	 * @return the name of the system that became visible
	 */
	public final String getSysName() {
		return sysName;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " - "+sysName;
	}
}
