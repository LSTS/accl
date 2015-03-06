package pt.lsts.accl.feedback;

import pt.lsts.accl.ACCL;
import pt.lsts.accl.listenners.SystemListChangeListener;
import pt.lsts.accl.managers.IMCManager;
import pt.lsts.accl.sys.Sys;
import pt.lsts.accl.sys.SystemList;
import pt.lsts.accl.util.AccuTimer;
import pt.lsts.imc.Heartbeat;
//import pt.lsts.imc.PlanDB;

import java.util.ArrayList;

import android.util.Log;

public class Heart implements SystemListChangeListener {
	public static final boolean DEBUG = true;
	public static final String TAG = "Heart";
	AccuTimer timer;
	ArrayList<Sys> vehicleList = new ArrayList<Sys>();
	SystemList sysList = ACCL.getInstance().getSystemList();
	IMCManager imm = ACCL.getInstance().getIMCManager();

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			sendHeartbeat();
		}
	};

	public Heart() {
		sysList.addSystemListChangeListener(this);
		timer = new AccuTimer(runnable, 1000);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void sendHeartbeat() {
        ArrayList<Sys> arrayListSys = sysList.getList();
		for (Sys sys : arrayListSys) {
			if (DEBUG)
				Log.v(TAG, "Beating... to sys:"+sys.getName());
			try {
                //imm.sendToSys(sys, "HeartBeat");//accu old version
                Heartbeat heartbeat = new Heartbeat();
                ACCL.getInstance().getIMCManager().sendToSys(sys,heartbeat);
            }catch(Exception e){
                Log.e(TAG,"sendHeartBeat exception: "+e.getMessage(),e);
                e.printStackTrace();
            }
		}
	}

	public void updateVehicleList(ArrayList<Sys> list) {
		vehicleList.clear();
		for (Sys s : list) {
			// if(!s.getType().equalsIgnoreCase("CCU"))
			vehicleList.add(s);
		}
	}

	@Override
	public void onSystemListChange(ArrayList<Sys> list) {
		updateVehicleList(list);
	}
}
