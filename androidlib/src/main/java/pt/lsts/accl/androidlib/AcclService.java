package pt.lsts.accl.androidlib;


import pt.lsts.accl.bus.AcclBus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 *
 * A Service that register itself uppon creation on the AcclBus.
 * Also provides an easy, hustle free, start method.
 *
 * Created by jloureiro on 18-08-2015
 *
 */
public class AcclService extends Service {

    public static String TAG = "TAG";
    public IBinder binder;

    /**
     *
     * Create service and register itself on AcclBus.
     *
     */
    public AcclService(){
        TAG = this.getClass().getSimpleName();
        register();
    }

    /**
     *
     * Standard Binder.
     *
     * @param intent not used.
     * @return the newly standard created Binder.
     *
     */
    @Override
    public IBinder onBind(Intent intent) {
        binder = new Binder();
        return binder;
    }

    /**
     *
     * Start the Service from a Context.
     *
     * @param context The Context to start the Service from.
     *
     */
    public void start(Context context){
        Intent intent = new Intent(context, this.getClass());
        onStartCommand(intent, 0, 0);
        onBind(intent);
    }

    /**
     *
     * Register itself on AcclBus.
     *
     */
    public void register(){
        AcclBus.register(this);
    }

    /**
     *
     * Unregister itself from AcclBus.
     *
     */
    public void unregister(){
        AcclBus.unregister(this);
    }

}
