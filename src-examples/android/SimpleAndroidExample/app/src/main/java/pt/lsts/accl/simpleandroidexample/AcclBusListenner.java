package pt.lsts.accl.simpleandroidexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;
import pt.lsts.imc.PlanControlState;

/**
 * Created by jloureiro on 02-07-2015.
 */
public class AcclBusListenner extends Service {

    public static final String TAG = "AcclBusListenner";

    private EventMainSystemSelected selectedSystem = null;

    @Produce
    public EventMainSystemSelected produceSelectedSystem() {
        return selectedSystem;
    }

    @Subscribe
    public void on(EventSystemVisible event) {
        if (event.getSysName().startsWith("lauv"))
            AcclBus.connect(event.getSysName());

        Log.v(TAG, event.getSysName()+" became visible");
    }

    @Subscribe
    public void on(EventSystemConnected event) {

        Log.v(TAG, event.getSysName() + " is now connected");

        if (selectedSystem == null) {
            Log.v(TAG, "selecting " + event.getSysName() + " as main system");
            selectedSystem = new EventMainSystemSelected(event.getSysName());
            AcclBus.post(selectedSystem);
        }
    }


    @Subscribe
    public void on(PlanControlState state) {
        Log.v(TAG, state.getSourceName() + " is " + state.getState() + " (" + state.getPlanId() + ")");
    }

    @Subscribe
    public void on(EventSystemDisconnected event) {
        Log.v(TAG, event.toString());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
