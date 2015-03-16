package pt.lsts.accl.event;

import java.util.LinkedHashMap;

public class EventImcSystemState extends AbstractACCLEvent {

	private String sysName;
	private LinkedHashMap<String, String> errors = new LinkedHashMap<>();
		
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	public EventImcSystemState(String systemName) {
		super();
		this.setSysName(systemName);
	}
	
	public void clearErrors() {
		errors.clear();
	}
	
	public void addError(String subsystem, String error) {
		errors.put(subsystem, error);
	}
	
	public String getSysName() {
		return sysName;
	}

	public void setSysName(String system) {
		this.sysName = system;
	}

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		if (hasErrors()) {
			sb.append("System "+sysName+" reported errors {\n");
		}
		else {
			return "System "+sysName+" didn't report any errors.";
		}
	
		
		for (String err : errors.keySet()) {
			sb.append("\t"+err+": "+errors.get(err)+"\n");
		}
		sb.append("}\n");
	
		return sb.toString();
	}
	
}
