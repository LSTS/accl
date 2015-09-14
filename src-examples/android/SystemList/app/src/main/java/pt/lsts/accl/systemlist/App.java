package pt.lsts.accl.systemlist;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import pt.lsts.accl.bus.AcclBus;


/**
 * Created by jloureiro on 02-07-2015.
 */
public class App extends Application{

    private static String TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        Log.v(TAG, "App.onCreate()");
        AcclBus.bind("SystemListExample", 6006);
        Log.v(TAG, "AcclBus.bind()");

        AcclBusListenner acclBusListenner = new AcclBusListenner();
        acclBusListenner.start(this);

        AcclBus.register(acclBusListenner);
        Log.v(TAG, "AcclBus.register()");
    }


}
