package pt.lsts.accl.bus;


import pt.lsts.accl.event.EventSystemVisible;
import pt.lsts.accl.sys.Sys;
import pt.lsts.accl.sys.SysList;
import pt.lsts.accl.util.Log;

import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.imc.lsf.LsfMessageLogger;
import pt.lsts.imc.llf.LLFMessageLogger;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.PrintWriter;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


/**
 *
 *
 * The Singleton class accessible and used by the application.
 *
 */
public class AcclBus {


	private static Bus busInstance = null;
	private static ImcAdapter imcAdapter = null;
	private static HashSet<Integer> registeredListeners = new HashSet<Integer>();
	private static Sys mainSys = null;
	public static SysList sysList = new SysList();
	public static String localname;
	public static int localport;


	/**
	 *
	 * Create a single, synchronized Bus Instance of Otto Communications.
	 * Uses com.squareup.otto.Bus and android.os.Looper with com.squareup.otto.ThreadEnforcer.
	 *
	 * @return The bus created
	 *
	 * @see com.squareup.otto.Bus
	 * @see com.squareup.otto.ThreadEnforcer
	 */
	private synchronized static Bus bus() {
		if (busInstance == null) {
			try {
				Class.forName("android.os.Looper");
				//busInstance = new Bus("accl");
				busInstance = new Bus(ThreadEnforcer.ANY, "accl");
			}
			catch (Exception e) {
				busInstance = new Bus(ThreadEnforcer.ANY, "accl");
				System.err.println("WARNING - "+" Running in a desktop environment");
				post("WARNING - "+" Running in a desktop environment");
			}
		}
		return busInstance;	
	}

	/**
	 *
	 * Start listening for IMC messages and start announcing this node
	 * @param localname The name of this node in the IMC network
	 * @param localport The port to bind to. 
	 */
	public synchronized static void bind(String localname, int localport) {
		if (imcAdapter != null)
			imcAdapter.stop();
		imcAdapter = new AcclBus.ImcAdapter(localname, localport);
		post("INFO - "+"Binded with localname: "+localname+" in port: "+localport);
		AcclBus.localname = localname;
		AcclBus.localport = localport;
	}

	/**
	 *
	 * Post an event to the application
	 * @param event The event to be posted
	 */
	public static void post(Object event) {
		if (event!=null)
			bus().post(event);
	}

	/**
	 *
	 * Register a component with ACCL
	 * @param pojo An object that wishes to receive events
	 */
	public static void register(Object pojo) {
		try{
			bus().register(pojo);
		}
		catch(java.lang.IllegalArgumentException e){
			post("WARNING - " + "Attempted to register already registered listenner:" + pojo.toString());
			post("ERRROR - " + e.getMessage());
			return;
		}
		registeredListeners.add(pojo.hashCode());
		post("INFO - " + "Register new listenner:" + pojo.toString());
	}

	/**
	 *
	 * Unregister a component with ACCL
	 * @param pojo An object that wishes to stop receiving events
	 */
	public static synchronized void unregister(Object pojo) {
		try{
			bus().unregister(pojo);
		}
		catch(java.lang.IllegalArgumentException e){
			post("WARNING - " + "Attempted to unregister unregistered listenner:" + pojo.toString());
			post("ERRROR - " + e.getMessage());
			return;
		}
		registeredListeners.remove(pojo.hashCode());

		if (registeredListeners.isEmpty() && imcAdapter != null) {
			imcAdapter.stop();
			imcAdapter = null;
		}
		post("WARN - "+"Unregister listenner:"+pojo.toString());
	}

	/**
	 *
	 * Send and IMCMessage to a destination by its name
	 * @param msg The IMCMessage to be sent
	 * @param destinationName String with Destination system display name
	 * @return false if there's no imcAdpater or msg sending fails, true if msg is sent
	 */
	public static boolean sendMessage(IMCMessage msg, String destinationName) {
		if (imcAdapter == null)
			return false;
		post("VERBOSE - "+"Send "+msg.getAbbrev()+":\n"+msg.toString());
		boolean result = imcAdapter.sendMessage(msg, destinationName);
		return result;
	}

	/**
	 *
	 * Send and IMCMessage to a destination by its object Sys
	 * @param msg The IMCMessage to be sent
	 * @param destination Sys to send msg to
	 * @return false if there's no imcAdpater or msg sending fails, true if msg is sent
	 */
	public static boolean sendMessage(IMCMessage msg, Sys destination){
		return sendMessage(msg, destination.getName());
		/*
		 * !!! TODO: add more generic send that reccur on this one, like:
		 * sendMsgToSys, sendMsgToCCUs, sendMsgToALL, sendMsgToVehicles, ....
		*/
	}

	/**
	 * Send a IMCMessage to ALL vehicles
	 * @param msg The IMCMessage to be sent
	 * @return true if every IMCMessage was sent without erros, false otherwise.
	 */
	public static boolean sendMessageToAllVehicles(IMCMessage msg){
		boolean result=true;
		for (Sys s : sysList.getList()){
			if (s.isVehicle()==true){
				boolean bool = sendMessage(msg, s);
				if (bool==false) {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * Send a IMCMessage to ALL CCUs
	 * @param msg The IMCMessage to be sent
	 * @return true if every IMCMessage was sent without erros, false otherwise.
	 */
	public static boolean sendMessageToAllCCUs(IMCMessage msg){
		boolean result=true;
		for (Sys s : sysList.getList()){
			if (s.isCCU()==true){
				boolean bool = sendMessage(msg, s);
				if (bool==false) {
					result = false;
				}
			}
		}
		post("INFO - "+"Sent "+msg.getAbbrev()+" to ALL CCU");
		return result;
	}

	/**
	 * Send a IMCMessage to ALL UAVs
	 * @param msg The IMCMessage to be sent
	 * @return true if every IMCMessage was sent without erros, false otherwise.
	 */
	public static boolean sendMessageToAllUAVs(IMCMessage msg){
		boolean result=true;
		for (Sys s : sysList.getList()){
			if (s.isUAV()==true){
				boolean bool = sendMessage(msg, s);
				if (bool==false) {
					result = false;
				}
			}
		}
		post("INFO - "+"Sent "+msg.getAbbrev()+" to ALL UAV");
		return result;
	}

	/**
	 * Send a IMCMessage to ALL UUVs, also refered to as AUV or LAUV, (Light) Autnomous Underwater Vehicles
	 * @param msg The IMCMessage to be sent
	 * @return true if every IMCMessage was sent without erros, false otherwise.
	 */
	public static boolean sendMessageToAllUUVs(IMCMessage msg){
		boolean result=true;
		for (Sys s : sysList.getList()){
			if (s.isUUV()==true){
				boolean bool = sendMessage(msg, s);
				if (bool==false) {
					result = false;
				}
			}
		}
		post("INFO - "+"Sent "+msg.getAbbrev()+" to ALL UUV");
		return result;
	}

	/**
	 *
	 * Check if an IMCMessage is from Main system
	 *
	 * @param imcMessage The IMCMessage to be checked
	 * @return True if Source of Message is Main Sys, false otherwise.
	 *
	 * @see pt.lsts.imc.IMCMessage
	 */
	public static boolean isMsgFromMain(IMCMessage imcMessage){
		if (imcMessage.getSrc()==mainSys.getID())
			return true;
		return false;
	}

	/**
	 *
	 * Class responsabile for initiation of IMCProtocol and listenner and handler of sending and receiving IMCMessages
	 *
	 * @see pt.lsts.imc.net.IMCProtocol
	 * @see pt.lsts.imc.IMCMessage
	 * @see pt.lsts.neptus.messages.listener.MessageListener
	 */
	private static class ImcAdapter implements MessageListener<MessageInfo, IMCMessage> {

		private IMCProtocol imcProtocol;

		private Timer timer = new Timer(true);

		/**
		 *
		 * @param name The name given to this System to be sent on IMCMessages Announce.
		 * @param port the port where IMCMessages will be sent to, sent on IMCMessages Announce.
		 *
		 * @see pt.lsts.imc.IMCMessage
		 * @see pt.lsts.imc.Announce
		 */
		protected ImcAdapter(String name, int port) {
			imcProtocol = new IMCProtocol(name, port);
			// Do not connect automatically
			imcProtocol.setAutoConnect(":never:");
			imcProtocol.addMessageListener(this);

			/**
			 *
			 * Task responsabile for sending periodic IMCMessage Heartbeats to other systems so they keep this device alive
			 *
			 * @see pt.lsts.imc.IMCMessage
			 * @see pt.lsts.imc.Heartbeat
			 */
			TimerTask sendHeartbeats = new TimerTask() {

				@Override
				public void run() {
					synchronized (sysList.getList()) {
						for (Sys sys : sysList.getList())
							sendMessage(new Heartbeat(), sys.getName());
					}	
				}
			};

			/**
			 *
			 * Task responsabile for periodically eliminate sys from sysList that haven't sent message in over 30seconds
			 */
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

		/**
		 *
		 * Stop the IMCProtocol and tasks.
		 *
		 * @see pt.lsts.imc.net.IMCProtocol
		 */
		public void stop() {
			timer.cancel();
			imcProtocol.stop();
			post("WARN - "+"STOPPED");
		}

		/**
		 *
		 * The Lower level method for sending IMCMessages, all other ultimatly use this one
		 *
		 * @param msg The IMCMessage to be sent
		 * @param destination The name of the system to send msg to
		 * @return true if message sent, false otherwise
		 */
		public boolean sendMessage(IMCMessage msg, String destination) {
			boolean result = imcProtocol.sendMessage(destination, msg);
			return result;
		}

		/**
		 *
		 * Listenner de IMCMessages. All incoming messages trigger this method.
		 * @param info Generic meta information about the message including: time sent, type of sending, source.
		 * @param msg The IMCMessage itself in its generic type.
		 *
		 * @see pt.lsts.imc.IMCMessage
		 * @see pt.lsts.neptus.messages.listener.MessageInfo
		 *
		 */
		@Override
		public void onMessage(MessageInfo info, IMCMessage msg){
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

			// updated lastMsgReceived:
			Sys sys = sysList.getSys(ID);
			if (sys!=null)
				sys.setLastMsgReceived(msg);

			// post both specific and generic IMCMessage types:
			Class c = msg.getClass();
			AcclBus.post((c.cast(msg)));
			AcclBus.post(msg);

			post("VERBOSE - "+"Received "+msg.getAbbrev()+":\n"+msg.toString());
		}

		/**
		 *
		 * Finalise all imc comms
		 * @throws Throwable may throw erros and may be catched by developer on its application.
		 */
		@Override
		protected void finalize() throws Throwable {
			stop();
		}
	}

	public static void onPause(){
		AcclBus.disableLogging();
		Log.close();
		sysList.clear();
		imcAdapter.stop();
	}

	public static void onResume(){
		AcclBus.bind(AcclBus.localname, AcclBus.localport);
		AcclBus.enableLogging();
	}

	/**
	 *
	 * Set the Main/Active System
	 * @param sys The new Main System
	 */
	public static void setMainSys(Sys sys){
		AcclBus.mainSys = sys;
	}

	/**
	 *
	 * @return The Main/Active System, usually selected by user.
	 */
	public static Sys getMainSys(){
		return AcclBus.mainSys;
	}


	/**
	 * Enable Logging of IMCMessages
	 */
	public static void enableLogging(){
		AcclBus.imcAdapter.imcProtocol.setMessageLogger(Log.getIMessageLogger());
	}

	/**
	 * Disable Logging of IMCMessages
	 */
	public static void disableLogging(){
		AcclBus.imcAdapter.imcProtocol.setMessageLogger(null);
	}

}