package pt.lsts.accl.bus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;
import pt.lsts.accl.sys.Sys;
import pt.lsts.accl.sys.SysList;
import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class AcclBus {

	private static Bus busInstance = null;
	private static ImcAdapter imcInstance = null;
	private static HashSet<Integer> registeredListeners = new HashSet<Integer>();
	private static Sys activeSys = null;
	private static ArrayList<Sys> systemList = new ArrayList<Sys>();

	private synchronized static Bus bus() {
		if (busInstance == null) {
			try {
				Class.forName("android.os.Looper");
				//busInstance = new Bus("accl");
				busInstance = new Bus(ThreadEnforcer.ANY, "accl");
			}
			catch (Exception e) {
				busInstance = new Bus(ThreadEnforcer.ANY, "accl");
				System.err.println("WARNING: Running in a desktop environment");
			}
		}
		return busInstance;	
	}

	/**
	 * Start listening for IMC messages and start announcing this node
	 * @param localname The name of this node in the IMC network
	 * @param localport The port to bind to. 
	 */
	public synchronized static void bind(String localname, int localport) {
		if (imcInstance != null)
			imcInstance.stop();
		imcInstance = new AcclBus.ImcAdapter(localname, localport);
	}

/*
	/**
	 * Start sending {@link pt.lsts.imc.Heartbeat} to a system
	 * @param system The name of the system to connect to
	 */
/*
	public static void connect(String system) {
		if (imcInstance == null)			
			return;
		imcInstance.connect(system);		
	}
*/
	
	/*
	/**
	 * Stop sending {@link pt.lsts.imc.Heartbeat} to a system
	 * @param system The name of the system to be disconnected
	 */
	/*
	public static void disconnect(String system) {
		if (imcInstance == null)			
			return;
		imcInstance.disconnect(system);
	}
	*/


	/**
	 * Post an event to the application
	 * @param event The event to be posted
	 */
	public static void post(Object event) {
		if (event!=null)
			bus().post(event);
	}

	/**
	 * Register a component with ACCL
	 * @param pojo An object that wishes to receive events
	 */
	public static void register(Object pojo) {
		bus().register(pojo);
		registeredListeners.add(pojo.hashCode());

	}

	/**
	 * Unregister a component with ACCL
	 * @param pojo An object that wishes to stop receiving events
	 */
	public static synchronized void forget(Object pojo) {
		bus().unregister(pojo);
		registeredListeners.remove(pojo.hashCode());

		if (registeredListeners.isEmpty() && imcInstance != null) {
			imcInstance.stop();
			imcInstance = null;
		}

	}

	public static boolean send(IMCMessage msg, String destination) {
		if (imcInstance == null)
			return false;
		return imcInstance.send(msg, destination);
	}

	private static class ImcAdapter implements MessageListener<MessageInfo, IMCMessage> {

		private IMCProtocol imc;
		private LinkedHashSet<String> systemsToConnectTo = new LinkedHashSet<String>();
		private LinkedHashSet<String> systemsConnected = new LinkedHashSet<String>();
		private LinkedHashMap<String, Announce> lastAnnounce = new LinkedHashMap<String, Announce>();
		private SysList sysList = new SysList();

		private Timer timer = new Timer(true);

		protected ImcAdapter(String name, int port) {
			imc = new IMCProtocol(name, port);
			// Do not connect automatically
			imc.setAutoConnect(":never:");
			imc.addMessageListener(this);

			TimerTask sendHeartbeat = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList) {
						for (Sys sys : sysList.getList())
							imc.sendMessage(sys.getName(), new Heartbeat());
					}	
				}
			};
			
			TimerTask clearAnnounces = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList) {
						sysList.clearInnactiveSys();
					}
				}
			};
			
			timer.scheduleAtFixedRate(sendHeartbeat, 1000, 1000);
			timer.scheduleAtFixedRate(clearAnnounces, 30000, 30000);
		}

		public void stop() {
			timer.cancel();
			imc.stop();
		}

/*
		public void connect(String system) {
			synchronized (systemsToConnectTo) {
				systemsToConnectTo.add(system);
			}
		}

		public void disconnect(String system) {
			synchronized (systemsToConnectTo) {
				systemsToConnectTo.remove(system);
			}
		}
*/

		public boolean send(IMCMessage msg, String destination) {
			return imc.sendMessage(destination, msg);
			/**
			 * !!! TODO: add more generic send that reccur on this one, like:
			 * sendMsgToSys, sendMsgToCCUs, sendMsgToALL, sendMsgToVehicles, ....
			**/
		}

		@Override
		public void onMessage(MessageInfo info, IMCMessage msg) {
			// !!! TODO: scann all types of messages and post its specific type.
			// 		devs can they add listenner and receive such messages
			// Have different classes that handle all generic status messages:
			// position(EstState), speeds, FuelLevel, pathcontrolstate, maneuvercontrolstate, ...
			String source = msg.getSourceName();
			if (msg.getMgid() == Announce.ID_STATIC) {
				if (msg.getSrc() == imc.getLocalId())
					return;
				Announce announceMsg = (Announce) msg;
				Sys sys = new Sys(announceMsg);
				
				synchronized (sysList) {
					if (!sysList.contains(sys)){//add new system
						sysList.addSys(sys);
						AcclBus.post(new EventSystemVisible(sys));
					}
				}
					
			}
			else {// !!! TODO: figure out what this else does
				/*
				synchronized (systemsConnected) {
					if (!systemsConnected.contains(source)) {
						systemsConnected.add(source);
						AcclBus.post(new EventSystemConnected(source));
					}
				}
				*/			
			}

			AcclBus.post(msg);//have a generic method with IMCMessage or a specific one with its type?
		}

		@Override
		protected void finalize() throws Throwable {
			stop();
		}
	}

	public static void setActiveSys(Sys sys){
		AcclBus.activeSys = sys;
	}

	public static Sys getActiveSys(){
		return AcclBus.activeSys;
	}

}