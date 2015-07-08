package pt.lsts.accl.event;


import pt.lsts.accl.sys.Sys;


public class EventSystemDisconnected extends AbstractACCLEvent {

	/**
	 * The System that disconnected
	**/
	private Sys sys;
		
	public EventSystemDisconnected(Sys sys) {
		this.sys = sys;
	}

	/**
	 * @return the the system that disconnected
	 */
	public final Sys getSys() {
		return sys;
	}
	
	@Override
	public String toString() {
		// includes time since last message
		return super.toString()+ " - "+sys.getName()+ " disconnected - last message in "+sys.getLastMsgReceivedAgeInSeconds();
	}

}
