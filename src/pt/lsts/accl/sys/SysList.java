package pt.lsts.accl.sys;

import pt.lsts.accl.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

import java.util.ArrayList;

/**
 * Created by jloureiro on 06-07-2015.
 */
public class SysList {

	private ArrayList<Sys> sysArrayList;

	public SysList(){
		sysArrayList = new ArrayList<Sys>();
	}

	public void addSys(Sys sys){
		sysArrayList.add(sys);
	}

	public void removeSys(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				sysArrayList.remove(i);
				return;
			}
		}
	}

	public Sys containsSys(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				return sysArrayList.get(i);
			}
		}
		return null;
	}

	public boolean contains(Sys sys){
		for (int i=0;i<sysArrayList.size();i++){
			if (sysArrayList.get(i).isEqualTo(sys)){
				return true;
			}
		}
		return false;
	}

	public ArrayList<Sys> getList(){
		return sysArrayList;
	}

	public void clearInnactiveSys(){
		for (Sys sys : sysArrayList) {
			if (sys.getLastMsgReceivedAgeInSeconds() > 60) {
				removeSys(sys);
			}
		}
	}

}
