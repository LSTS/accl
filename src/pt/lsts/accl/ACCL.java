package pt.lsts.accl;

//import pt.lsts.accl.feedback.CallOut;
//import pt.lsts.accl.feedback.Heart;
//import pt.lsts.accl.feedback.HeartbeatVibrator;
import pt.lsts.accl.listenners.MainSysChangeListener;
import pt.lsts.accl.managers.SMSManager;
import pt.lsts.accl.managers.GPSManager;
import pt.lsts.accl.managers.IMCManager;
import pt.lsts.accl.subscribers.Announcer;
//import pt.lsts.accl.pos.LblBeaconList;
import pt.lsts.accl.subscribers.SMSManagerIMCSubscriber;
//import pt.lsts.accl.subscribers.HeartbeatVibratorIMCSubscriber;
import pt.lsts.accl.subscribers.IMCSubscriber;
//import pt.lsts.accl.subscribers.LblBeaconListIMCSubscriber;
import pt.lsts.accl.subscribers.SystemListIMCSubscriber;
import pt.lsts.accl.subscribers.SystemsUpdaterServiceIMCSubscriber;
import pt.lsts.accl.sys.Sys;
import pt.lsts.accl.sys.SystemList;
import pt.lsts.accl.util.MUtil;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Global Singleton of ACCL and necessary components
 * initiated in App extension of Application.
 * (Fork from ACCU)
 * 
 */

public class ACCL {

	private static final String TAG = "ACCL";

    public boolean UIThread = false;

	private static Context context;
	private static ACCL instance;
	private static Sys activeSys;

	private static IMCManager imcManager;
	public SystemList sysList;
	public SystemListIMCSubscriber systemListIMCSubscriber;
	
	public static Announcer announcer;
	public static SMSManager smsHandler;
	public static SMSManagerIMCSubscriber sMSManagerIMCSubscriber;
	public static GPSManager gpsManager;
	//public static HeartbeatVibrator hearbeatVibrator;
	//public static HeartbeatVibratorIMCSubscriber heartbeatVibratorIMCSubscriber;
	//public static Heart heart;
	//public static LblBeaconList lblBeaconList;
	//public static LblBeaconListIMCSubscriber lblBeaconListIMCSubscriber;
	public static SensorManager sensorManager;
    //private CallOut callOut;

	private static ArrayList<MainSysChangeListener> mainSysChangeListeners;
	public String broadcastAddress;
	public boolean started = false;
	public SharedPreferences sharedPreferences;
    private Bus bus;

    private SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber;

    private static Integer requestId = 0xFFFF; // Request ID for quick plan

    public  enum MODE {
        NONE,//MainActivity, in between Activities, all messages are handled
        SYSTEMLIST,//only announces are handled
        CHECKLIST,//only messages from Active Sys
        SETTINGS,//no messages handled
        MANUAL,//only messages from Active Sys
        AUTO,//all?
        OTHER//should not be used
    }

    private MODE mode=MODE.NONE;

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
        Log.v(TAG,"ACCL.mode changed to: "+mode.toString());
    }

    private ACCL(Context context) {
		this.context = context;
        setMode(MODE.NONE);
		initIMCManager();
		initBroadcast();
		initGPSManager(context);
		initPreferences(context);
		initSystemsList();
		initAnnouncer();
		initSubscribers(context);
	}
	
	public void addSubscriber(IMCSubscriber sub){
		imcManager.addSubscriberToAllMessages(sub);
	}
	public void addSubscriber(IMCSubscriber sub, String[] abbrevNameList){
		imcManager.addSubscriber(sub, abbrevNameList);
	}
    public void removeSubscriber(IMCSubscriber sub){
        imcManager.removeSubscriberToAll(sub);
    }
	
	public void initBroadcast(){
		try {
			broadcastAddress = MUtil.getBroadcastAddress(context);
		} catch (IOException e) {
			Log.e(TAG, ACCL.class.getSimpleName()
					+ ": Couldn't get Brodcast address", e);
		}
	}
	
	public void initSystemsList(){
		mainSysChangeListeners = new ArrayList<MainSysChangeListener>();
		sysList = new SystemList(imcManager);
		systemListIMCSubscriber = new SystemListIMCSubscriber(sysList);
	}
	
	public void initAnnouncer(){
		announcer = new Announcer(imcManager, broadcastAddress, "224.0.75.69");
	}
	
	public void initGPSManager(Context context){
		gpsManager = new GPSManager(context);
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}
	
	public void initPreferences(Context context){
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bus = new Bus(ThreadEnforcer.ANY);
	}
	
	public void initIMCManager(){
		imcManager = new IMCManager();
		imcManager.startComms();
	}
	
	public void initSubscribers(Context context){
		smsHandler = new SMSManager(context);
		sMSManagerIMCSubscriber = new SMSManagerIMCSubscriber(smsHandler);
		
		//hearbeatVibrator = new HeartbeatVibrator(context, imcManager);
		//heartbeatVibratorIMCSubscriber = new HeartbeatVibratorIMCSubscriber(hearbeatVibrator);
		
		addInitialSubscribers();
	}
	
	public void addInitialSubscribers(){
		//addSubscriber(heartbeatVibratorIMCSubscriber, heartbeatVibratorIMCSubscriber.SUBSCRIBED_MSGS);
		addSubscriber(sMSManagerIMCSubscriber, sMSManagerIMCSubscriber.SUBSCRIBED_MSGS);
		addSubscriber(systemListIMCSubscriber);
		//addSubscriber(lblBeaconListIMCSubscriber, lblBeaconListIMCSubscriber.SUBSCRIBED_MSGS);
	}	

	public void load() {
		Log.i(TAG, ACCL.class.getSimpleName() + ": load");
		//heart = new Heart();
		//addLblSubscriber();
	}
	
	/*public void addLblSubscriber(){
		lblBeaconList = new LblBeaconList();
		lblBeaconListIMCSubscriber = new LblBeaconListIMCSubscriber(lblBeaconList);
	}*/

	public void start() {
		Log.i(TAG, ACCL.class.getSimpleName() + ": start");
		if (!started) {
			imcManager.startComms();
			announcer.start();
			sysList.start();
			//heart.start();
			started = true;
            startAndBindSystemsUpdaterIMCSubscriber();
		} else
			Log.e(TAG, ACCL.class.getSimpleName()
					+ ": ACCL ERROR: Already Started ACCL Global");
	}

	public void pause() {
		Log.i(TAG, ACCL.class.getSimpleName() + ": pause");
		if (started) {
			imcManager.killComms();
			announcer.stop();
			sysList.stop();
			//heart.stop();
			smsHandler.stop();
			started = false;
		} else
			Log.e(TAG, ACCL.class.getSimpleName()
					+ ": ACCL ERROR: ACCL Global already stopped");
	}

	public static ACCL getInstance(Context context) {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getInstance(context)");
		if (instance == null) {
			instance = new ACCL(context);
		}
		return instance;
	}

	public static ACCL getInstance() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getInstance");
		return instance;
	}

	public Sys getActiveSys() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getActiveSys");
		return activeSys;
	}

	public void setActiveSys(Sys activeS) {
		Log.v(TAG, ACCL.class.getSimpleName() + ": setActiveSys");
		activeSys = activeS;
		notifyMainSysChange();
	}

	public IMCManager getIMCManager() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getIMCManager");
		return imcManager;
	}

	public SystemList getSystemList() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getSystemList");
		return sysList;
	}

	public GPSManager getGpsManager() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getGpsManager");
		return gpsManager;
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	/*public LblBeaconList getLblBeaconList() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getLblBeaconList");
		return lblBeaconList;
	}*/

    public SystemsUpdaterServiceIMCSubscriber getSystemsUpdaterServiceIMCSubscriber() {
        return systemsUpdaterServiceIMCSubscriber;
    }

    public void setSystemsUpdaterServiceIMCSubscriber(SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber) {
        this.systemsUpdaterServiceIMCSubscriber = systemsUpdaterServiceIMCSubscriber;
    }
    
    public Bus getBus() {
        return bus;
    }

    /*public CallOut getCallOut() {
        return callOut;
    }


    public void setCallOut(CallOut callOut) {
        this.callOut = callOut;
    }*/

	// Main System listeners list related code
	public void addMainSysChangeListener(MainSysChangeListener listener) {
		Log.v(TAG, ACCL.class.getSimpleName() + ": addMainSysChangeListener");
		mainSysChangeListeners.add(listener);
	}

	public void removeMainSysChangeListener(MainSysChangeListener listener) {
		Log.v(TAG, ACCL.class.getSimpleName() + ": removeMainSysChangeListener");
		mainSysChangeListeners.remove(listener);
	}

	private static void notifyMainSysChange() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": notifyMainSysChange");
		for (MainSysChangeListener l : mainSysChangeListeners) {
			l.onMainSysChange(activeSys);
		}
	}

	public SharedPreferences getPrefs() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getPrefs");
		return sharedPreferences;
	}

	public boolean isStarted() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": isStarted");
		return started;
	}

	/**
	 * @return the next requestId
	 */
	public int getNextRequestId() {
		Log.v(TAG, ACCL.class.getSimpleName() + ": getNextRequestId");
		synchronized (requestId) {
			++requestId;
			if (requestId > 0xFFFF)
				requestId = 0;
			if (requestId < 0)
				requestId = 0;
			return requestId;
		}
	}
	
	public static Context getContext() {
		return context;
	}
	
	public void addPreferencesListenner(OnSharedPreferenceChangeListener listener){
		ACCL.getInstance().sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        Log.i(TAG,"ACCL.getInstance().sharedPreferences.registerOnSharedPreferenceChangeListener(listener);");
	}

    public void startAndBindSystemsUpdaterIMCSubscriber(){
        SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber = new SystemsUpdaterServiceIMCSubscriber();
        Intent intent = new Intent(getContext(),SystemsUpdaterServiceIMCSubscriber.class);
        systemsUpdaterServiceIMCSubscriber.onStartCommand(intent,0,0);
        systemsUpdaterServiceIMCSubscriber.onBind(intent);
    }
	
}
