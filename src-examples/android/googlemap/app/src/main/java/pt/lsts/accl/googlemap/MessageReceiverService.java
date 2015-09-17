package pt.lsts.accl.googlemap;


import pt.lsts.accl.android.AcclService;
import pt.lsts.accl.event.EventSystemUpdated;
import pt.lsts.imc.IMCMessage;

import android.util.Log;

import com.squareup.otto.Subscribe;


/**
 * Created by jloureiro on 16-09-2015.
 */
public class MessageReceiverService extends AcclService{

    MapsActivity mapsActivity;

    /**
     * Create a Service giving the {@link MapsActivity} that created it to have its methods called.
     *
     * @param mapsActivity The {@link MapsActivity} that created this service uppon which changes will be triggered.
     */
    public MessageReceiverService(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    /**
     * Update a system's marker.
     *
     * @param e The Event that triggered this update.
     */
    @Subscribe
    public void onSysUpdate(EventSystemUpdated e){
        Log.v(TAG, "EventSystemUpdated: " + e.getSys().getName().toString());
        mapsActivity.updateMarker(e.getSys());

    }

    /**
     * Receive IMCMessages output them to {@link Log}.
     * This can be used for debugging.
     *
     * @param msg The received {@link IMCMessage}.
     */
    @Subscribe
    public void onMsgReceived(IMCMessage msg){
        Log.v(TAG, "Received asddasds msg type: "+msg.getMessageType().getFullName()+" from: "+ msg.getSourceName());
    }

    /**
     * Receive Strings posted by {@link pt.lsts.accl.bus.AcclBus}.
     * These contain messages for different purposes:
     * DEBUG
     * INFO
     * VERBOSE
     * WARNING
     * ERROR
     *
     * @param s The String message posted by {@link pt.lsts.accl.bus.AcclBus}.
     */
    @Subscribe
    public void onAcclBusString(String s){
        if (s.charAt(0)=='D');
        Log.d("AcclBusMSG", s);
    }

}
