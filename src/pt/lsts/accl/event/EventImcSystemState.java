package pt.lsts.accl.event;

import java.util.LinkedHashMap;

//import pt.lsts.imc.EntityState;

public class EventImcSystemState {

	private String sys_name;
	private LinkedHashMap<String, String> errors = new LinkedHashMap<>();
		
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	public EventImcSystemState(String name) {
		super();
		this.setSysName(name);
	}
	
	public void clearErrors() {
		errors.clear();
	}
	
	public void addError(String subsystem, String error) {
		errors.put(subsystem, error);
	}
	
	public String getSysName() {
		return sys_name;
	}

	public void setSysName(String sys_name) {
		this.sys_name = sys_name;
	}

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		if (hasErrors()) {
			sb.append("System "+sys_name+" reported errors {\n");
		}
		else {
			return "System "+sys_name+" didn't report any errors.";
		}
	
		
		for (String err : errors.keySet()) {
			sb.append("\t"+err+": "+errors.get(err)+"\n");
		}
		sb.append("}\n");
	
		return sb.toString();
	}
	
	//public static void main(String[] args) {
		//EntityState s = new EntityState();
		//s.set
		//EventImcSystemState state = new EventImcSystemState("lauv-seacon-1");
		//state.addError("Maneuver ETA", "Maneuver ETA is above 60 seconds");
		//System.out.println(state);
	//}
}
