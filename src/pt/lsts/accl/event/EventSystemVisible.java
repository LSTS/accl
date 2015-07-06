package pt.lsts.accl.event;

import pt.lsts.accl.sys.Sys;

public class EventSystemVisible extends AbstractACCLEvent {

	private Sys sys;
		
	public EventSystemVisible(Sys sys) {
		this.sys = sys;
	}

	/**
	 * @return the name of the system that disconnected
	 */
	public final Sys getSys() {
		return sys;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " - "+sys.getName();
	}
}
