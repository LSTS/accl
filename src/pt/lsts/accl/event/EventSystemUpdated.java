package pt.lsts.accl.event;


import pt.lsts.accl.sys.Sys;


public class EventSystemUpdated extends AbstractACCLEvent {

	private Sys sys;
	
	public EventSystemUpdated(Sys sys) {
		this.sys = sys;
	}

	/**
	 * @return the system
	 */
	public final Sys getSys() {
		return sys;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - "+sys.getName();
	}	
}
