package pt.lsts.accl.sys;

import pt.lsts.accl.listenners.SystemListChangeListener;
import pt.lsts.accl.managers.IMCManager;
import pt.lsts.accl.util.AccuTimer;

import java.util.ArrayList;

import com.squareup.otto.Subscribe;

import android.util.Log;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

public class SystemList {

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false;
	Announce announce = new Announce();
	private String sysName = announce.getSysName();

	private EventMainSystemSelected selectedSystem = null;

	@Subscribe
	public void on(Announce ann) {
		Log.i("ANNOUNCE", ann.getSourceName());
	}

	@Subscribe
	public void on(EventSystemConnected event) {
		System.out.println(event.getSysName() + " is now connected");
		if (selectedSystem == null) {
			System.out.println("selecting " + event.getSysName()
					+ " as main system");
			selectedSystem = new EventMainSystemSelected(event.getSysName());
			AcclBus.post(selectedSystem);
		}
	}

	ArrayList<Sys> sysList = new ArrayList<Sys>();
	ArrayList<SystemListChangeListener> listeners = new ArrayList<SystemListChangeListener>();

	AccuTimer timer;
	Runnable task = new Runnable() {
		public void run() {
			AcclBus.bind("accldroid", 6009);
			AcclBus.register(this);
			checkConnection();
		}
	};

	public SystemList(IMCManager imm) {
		timer = new AccuTimer(task, 1000);
	}

	// Timed action, in this case checking connection state trough Heartbeat
	// FIXME Heartbeat is not that good of a metric used simply like that
	private void checkConnection() {
		long currentTime = System.currentTimeMillis();

		if (DEBUG)
			Log.v("SystemList", "Checking Connections");

		for (Sys s : sysList) {
			if (DEBUG)
				Log.i("Log", s.getName() + " - "
						+ (currentTime - s.lastMessageReceived));
			if ((currentTime - s.lastMessageReceived) > CONNECTED_TIME_LIMIT
					&& s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			} else if ((currentTime - s.lastMessageReceived) < CONNECTED_TIME_LIMIT
					&& !s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			}
		}
	}

	// ------add Changed Listenner
	public void addSystemListChangeListener(SystemListChangeListener l) {
		listeners.add(l);
	}

	// ------remove Changed Listenner
	public void removeChangedListenner(SystemListChangeListener l) {
		listeners.remove(l);
	}

	public void changeList(ArrayList<Sys> list) {
		// Pass the new list to the listeners
		for (SystemListChangeListener l : listeners)
			l.onSystemListChange(list);
	}

	public boolean containsSysName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean containsSysId(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return true;
		}
		return false;
	}

	public Sys findSysByName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return sysList.get(c);
		}
		return null;
	}

	public Sys findSysById(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return sysList.get(c);
		}
		return null;
	}

	public ArrayList<Sys> getList() {
		return sysList;
	}

	public ArrayList<String> getNameList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			list.add(s.getName());
		}
		return list;
	}
	
	public ArrayList<String> getAddressList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			list.add(s.getAddress());
		}
		return list;
	}

	public ArrayList<String> getNameListByType(String type) {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			if (s.getType().equals(type))
				list.add(s.getName());
		}
		return list;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public String getActiveSys() {
		return sysName;
	}

	public Sys findSysByAddress(String address) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getAddress().equalsIgnoreCase(address))
				return sysList.get(c);
		}
		return null;
	}

	// ------has Sys
	public Boolean hasSys() {
		Boolean hasSys=false;
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().isEmpty())
				return hasSys;
			else return hasSys=true;
		}
		return hasSys;
	}

	// ------is Empty
	public Boolean isEmpty() {
		Boolean empty = false;
		if (sysName != "") {
			empty = false;
		} else {
			empty = true;
		}
		return empty;
	}

	// ------get Vehicles
	public String getVehicles() {
		String vehicles = Sys.SYS_TYPE.UUV.toString();
		vehicles += "\n" + Sys.SYS_TYPE.USV.toString();
		vehicles += "\n" + Sys.SYS_TYPE.UAV.toString();
		vehicles += "\n" + Sys.SYS_TYPE.UGV.toString();
		return vehicles;
	}

	// ------get CCUs
	public String getCCUs() {
		return Sys.SYS_TYPE.CCU.toString();
	}

	// ------send Broadcast Message To All
	public void sendBroadcastMessageToAll(IMCMessage msg) {
		AcclBus.send(msg, sysName);
	}

	// ------send Broadcast Message To Vehicles
	public void sendBroadcastMessageToVehicles(IMCMessage msg) {
		AcclBus.send(msg, sysName);
	}

	// ------send Broadcast Message To CCUs
	public void sendBroadcastMessageToCCUs(IMCMessage msg) {
		AcclBus.send(msg, sysName);
	}

}
