package pt.lsts.accl.sys;

import pt.lsts.accl.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

import java.util.ArrayList;

/**
 * The List of Systems active.
 * Used with synchronized to ensure correct usage and no duplicates or outdated results
 *
 * Created by jloureiro on 06-07-2015.
 */
public class SysList {

	/**
	 * The ArrayList with the systems itself upon the methods are applied.
	 */
	private ArrayList<Sys> sysArrayList;

	public SysList(){
		sysArrayList = new ArrayList<Sys>();
	}

	/**
	 * Add a system to the List
	 * @param sys The system to be added
	 */
	public void addSys(Sys sys){
		sysArrayList.add(sys);
	}

	/**
	 * Remove a system if it exists. Usually used automatically by a periodic task if no message has been received from this system in a pre established amount of time.
	 * @param sys The system to be removed
	 */
	public void removeSys(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				sysArrayList.remove(i);
				return;
			}
		}
	}

	/**
	 * Search for a system by its name.
	 * @param sysName The name of the system to search for.
	 * @return The sys, null if it doesn't exist.
	 */
	public Sys getSys(String sysName){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).getName().equals(sysName)){
				return sysArrayList.get(i);
			}
		}
		return null;
	}

	/**
	 * Find if a system exist and get it if it does.
	 * @param sys The system to search for.
	 * @return The system, null if it doesn't exist.
	 */
	public Sys containsSys(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				return sysArrayList.get(i);
			}
		}
		return null;
	}

	/**
	 * Find if a system exists and return a boolean.
	 * @param sys The system to search for.
	 * @return true if system exists, false otherwise.
	 */
	public boolean contains(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the actual ArrayList. Not usual. Not advisable.
	 * @return The ArrayList containing the systems.
	 */
	public ArrayList<Sys> getList(){
		return sysArrayList;
	}

	/**
	 * Remove system which haven't received a message in over 60seconds/1min from the list.
	 */
	public void clearInnactiveSys(){
		for (Sys sys : sysArrayList) {
			if (sys.getLastMsgReceivedAgeInSeconds() > 60) {
				removeSys(sys);
			}
		}
	}

}
