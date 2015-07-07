package pt.lsts.accl.event;


import pt.lsts.accl.sys.Sys;


public class EventSystemConnected extends AbstractACCLEvent {

	private Sys sys;
		
	public EventSystemConnected(Sys sys) {
		this.sys = sys;
	}

	/**
	 * @return the system that became visible
	 */
	public final Sys getSys() {
		return sys;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " - "+sys.getName();
	}
}
