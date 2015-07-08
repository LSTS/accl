package pt.lsts.accl.event;


import pt.lsts.accl.sys.Sys;


public class EventMainSystemSelected extends AbstractACCLEvent {

	private Sys sys;
	
	public EventMainSystemSelected(Sys sys) {
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
		return super.toString() + " - "+sys.getName()+" selected as Main system";
	}	
}
