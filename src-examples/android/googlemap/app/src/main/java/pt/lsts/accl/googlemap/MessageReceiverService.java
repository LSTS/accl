package pt.lsts.accl.googlemap;

import android.util.Log;

import com.squareup.otto.Subscribe;

import pt.lsts.accl.android.AcclActivity;
import pt.lsts.accl.android.AcclService;
import pt.lsts.accl.event.EventSystemUpdated;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 16-09-2015.
 */
public class MessageReceiverService extends AcclService{

    MapsActivity mapsActivity;

    public MessageReceiverService(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    @Subscribe
    public void onSysUpdate(EventSystemUpdated e){
        Log.v(TAG, "EventSystemUpdated: " + e.getSys().getName().toString());
        mapsActivity.updateMarker(e.getSys());

    }

    @Subscribe
    public void onMsgReceived(IMCMessage msg){
        Log.v(TAG, "Received asddasds msg type: "+msg.getMessageType().getFullName()+" from: "+ msg.getSourceName());
    }

    @Subscribe
    public void onAcclBusString(String s){
        if (s.charAt(0)=='D');
        Log.d("AcclBusMSG", s);
    }
}
