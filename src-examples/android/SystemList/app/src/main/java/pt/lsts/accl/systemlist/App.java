package pt.lsts.accl.systemlist;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import pt.lsts.accl.bus.AcclBus;

/**
 * Created by jloureiro on 02-07-2015.
 */
public class App extends Application{

    private static final String TAG = "App";
    AcclBus acclBus;//the main interaction with the lib ACCL

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "App.onCreate()");
        AcclBus.bind("SystemListExample", 6006);
        Log.v(TAG, "AcclBus.bind()");
        AcclBusListenner acclBusListenner = new AcclBusListenner();
        Intent intent = new Intent(this,AcclBusListenner.class);
        acclBusListenner.onStartCommand(intent,0,0);
        acclBusListenner.onBind(intent);
        AcclBus.register(acclBusListenner);
        Log.v(TAG, "AcclBus.register()");

    }

    public AcclBus getAcclBus() {
        return acclBus;
    }

    public void setAcclBus(AcclBus acclBus) {
        this.acclBus = acclBus;
    }
}
