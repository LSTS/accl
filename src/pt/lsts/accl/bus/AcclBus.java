package pt.lsts.accl.bus;


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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


public class AcclBus {

	private static Bus busInstance = null;
	private static ImcAdapter imcAdapter = null;
	private static HashSet<Integer> registeredListeners = new HashSet<Integer>();
	private static Sys mainSys = null;
	public static SysList sysList = new SysList();

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
		if (imcAdapter != null)
			imcAdapter.stop();
		imcAdapter = new AcclBus.ImcAdapter(localname, localport);
	}

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
	public static synchronized void unregister(Object pojo) {
		bus().unregister(pojo);
		registeredListeners.remove(pojo.hashCode());

		if (registeredListeners.isEmpty() && imcAdapter != null) {
			imcAdapter.stop();
			imcAdapter = null;
		}

	}

	public static boolean sendMessage(IMCMessage msg, String destinationName) {
		if (imcAdapter == null)
			return false;
		return imcAdapter.sendMessage(msg, destinationName);
	}

	public static boolean sendMessage(IMCMessage msg, Sys destination){
		return sendMessage(msg, destination.getName());
	}

	private static class ImcAdapter implements MessageListener<MessageInfo, IMCMessage> {

		private IMCProtocol imcProtocol;

		private Timer timer = new Timer(true);

		protected ImcAdapter(String name, int port) {
			imcProtocol = new IMCProtocol(name, port);
			// Do not connect automatically
			imcProtocol.setAutoConnect(":never:");
			imcProtocol.addMessageListener(this);

			TimerTask sendHeartbeats = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList.getList()) {
						for (Sys sys : sysList.getList())
							imcProtocol.sendMessage(sys.getName(), new Heartbeat());
					}	
				}
			};
			
			TimerTask clearInnactiveSys = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList.getList()) {
						sysList.clearInnactiveSys();
					}
				}
			};
			
			timer.scheduleAtFixedRate(sendHeartbeats, 1000, 1000);
			timer.scheduleAtFixedRate(clearInnactiveSys, 30000, 30000);
		}

		public void stop() {
			timer.cancel();
			imcProtocol.stop();
		}

		public boolean sendMessage(IMCMessage msg, String destination) {
			return imcProtocol.sendMessage(destination, msg);
			/**
			 * !!! TODO: add more generic send that reccur on this one, like:
			 * sendMsgToSys, sendMsgToCCUs, sendMsgToALL, sendMsgToVehicles, ....
			**/
		}

		@Override
		public void onMessage(MessageInfo info, IMCMessage msg) {
			// !!! TODO: scann new types of messages to keep up the status of the vehicles and other system
			// messages to scan: EstimatedState, True/IndicatedSpeeds, Path/PlanControlState ...
			// Maybe seperate this scanning in different methods like in ASA.
			
			String source = msg.getSourceName();
			int ID = msg.getSrc();
			if (msg.getMgid() == Announce.ID_STATIC) {//process Announce create new Sys and add to SysList
				synchronized (sysList.getList()){
					if (sysList.contains(ID)==false){//add new system
						if (ID == imcProtocol.getLocalId())
							return;
						Announce announceMsg = (Announce) msg;
						Sys sys = new Sys(announceMsg);
							
						sysList.addSys(sys);
						AcclBus.post(new EventSystemVisible(sys));
					}
				}
			}

			//updated lastMsgReceived:
			Sys sys = sysList.getSys(ID);
			if (sys!=null)
				sys.setLastMsgReceived(msg);

			// send specific and generic IMC Message types:
			Class c = msg.getClass();
			AcclBus.post((c.cast(msg)));
			AcclBus.post(msg);
		}

		@Override
		protected void finalize() throws Throwable {
			stop();
		}
	}


	public static void setMainSys(Sys sys){
		AcclBus.mainSys = sys;
	}

	public static Sys getMainSys(){
		return AcclBus.mainSys;
	}

}