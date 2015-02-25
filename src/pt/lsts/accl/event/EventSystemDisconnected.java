package pt.lsts.accl.event;

public class EventSystemDisconnected extends AbstractACCLEvent {

	private String sysName;
		
	public EventSystemDisconnected(String systemName) {
		this.sysName = systemName;
	}

	/**
	 * @return the name of the system that disconnected
	 */
	public final String getSysName() {
		return sysName;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " - "+sysName;
	}

}
