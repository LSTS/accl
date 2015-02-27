package pt.lsts.accl.bus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;
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
	
	/**
	 * Start sending {@link pt.lsts.imc.Heartbeat} to a system
	 * @param system The name of the system to connect to
	 */
	public static void connect(String system) {
		if (imcInstance == null)			
			return;
		imcInstance.connect(system);		
	}
	
	/**
	 * Stop sending {@link pt.lsts.imc.Heartbeat} to a system
	 * @param system The name of the system to be disconnected
	 */
	public static void disconnect(String system) {
		if (imcInstance == null)			
			return;
		imcInstance.disconnect(system);		
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
	}
	
	/**
	 * Unregister a component with ACCL
	 * @param pojo An object that wishes to stop receiving events
	 */
	public static void forget(Object pojo) {
		bus().unregister(pojo);
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

		private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		protected ImcAdapter(String name, int port) {
			imc = new IMCProtocol(name, port);
			// Do not connect automatically
			imc.setAutoConnect(":never:");
			imc.addMessageListener(this);

			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					synchronized (systemsToConnectTo) {
						for (String s : systemsToConnectTo)
							imc.sendMessage(s, new Heartbeat());					
					}				
				}
			}, 1, 1, TimeUnit.SECONDS);

			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {

					ArrayList<Announce> announces = new ArrayList<Announce>();
					announces.addAll(lastAnnounce.values());

					for (Announce a : announces) {
						if (a.getAgeInSeconds() > 60) {
							lastAnnounce.remove(a.getSourceName());
							AcclBus.post(new EventSystemDisconnected(a.getSourceName()));
						}
					}
				}
			}, 30, 30, TimeUnit.SECONDS);
		}

		public void stop() {
			imc.stop();
		}

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
		
		public boolean send(IMCMessage msg, String destination) {
			return imc.sendMessage(destination, msg);
		}

		@Override
		public void onMessage(MessageInfo info, IMCMessage msg) {
			
			String source = msg.getSourceName();
			if (msg.getMgid() == Announce.ID_STATIC) {
				if (msg.getSrc() == imc.getLocalId())
					return;
				Announce before;
				synchronized (lastAnnounce) {
					before = lastAnnounce.get(source);
					lastAnnounce.put(source, (Announce)msg);	
				}

				if (before == null)
					AcclBus.post(new EventSystemVisible(source));
			}
			else {
				synchronized (systemsConnected) {
					if (!systemsConnected.contains(source)) {
						systemsConnected.add(source);
						AcclBus.post(new EventSystemConnected(source));
					}
				}			
			}
			
			AcclBus.post(msg);
		}

		@Override
		protected void finalize() throws Throwable {
			stop();
		}
	}
}