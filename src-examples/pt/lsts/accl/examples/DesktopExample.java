package pt.lsts.accl.examples;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;
import pt.lsts.imc.PlanControlState;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

public class DesktopExample {
	
	private EventMainSystemSelected selectedSystem = null;
	
	@Produce
	public EventMainSystemSelected produceSelectedSystem() {
		return selectedSystem;
	}
	
	@Subscribe 
	public void on(EventSystemVisible event) {
		if (event.getSysName().startsWith("lauv"))
			AcclBus.connect(event.getSysName());
		
		System.out.println(event.getSysName()+" became visible");
	}
	
	@Subscribe 
	public void on(EventSystemConnected event) {
		
		System.out.println(event.getSysName()+" is now connected");
		
		if (selectedSystem == null) {
			System.out.println("selecting "+event.getSysName()+" as main system");
			selectedSystem = new EventMainSystemSelected(event.getSysName());
			AcclBus.post(selectedSystem);
		}
	}
	
	
	@Subscribe 
	public void on(PlanControlState state) {
		System.out.println(state.getSourceName()+" is "+state.getState()+" ("+state.getPlanId()+")");
	}
	
	@Subscribe 
	public void on(EventSystemDisconnected event) {
		System.out.println(event);
	}
	
	public static void main(String[] args) {
		AcclBus.bind("DesktopExample", 6006);
		AcclBus.register(new DesktopExample());
		AcclBus.register(new DesktopExample2());
	}
}
