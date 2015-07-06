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
	private static ImcAdapter imcAdapter = null;
	private static HashSet<Integer> registeredListeners = new HashSet<Integer>();
	private static Sys mainSys = null;
	private static ArrayList<Sys> sysList = new ArrayList<Sys>();

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
		private SysList sysList = new SysList();

		private Timer timer = new Timer(true);

		protected ImcAdapter(String name, int port) {
			imcProtocol = new IMCProtocol(name, port);
			// Do not connect automatically
			imcProtocol.setAutoConnect(":never:");
			imcProtocol.addMessageListener(this);

			TimerTask sendHeartbeats = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList) {
						for (Sys sys : sysList.getList())
							imcProtocol.sendMessage(sys.getName(), new Heartbeat());
					}	
				}
			};
			
			TimerTask clearInnactiveSys = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList) {
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
			// !!! TODO: scann all types of messages and post its specific type.
			// 		devs can they add listenner and receive such messages
			// Have different classes that handle all generic status messages:
			// position(EstState), speeds, FuelLevel, pathcontrolstate, maneuvercontrolstate, ...
			String source = msg.getSourceName();
			if (msg.getMgid() == Announce.ID_STATIC) {
				if (msg.getSrc() == imcProtocol.getLocalId())
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
			else {//not an announce just update last msg for now
				synchronized (sysList) {
					Sys sys = sysList.getSys(msg.getSourceName());
					sys.setLastMsgReceived(msg);
				}	
			}

			AcclBus.post(msg);//have a generic method with IMCMessage or a specific one with its type?
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