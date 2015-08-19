package pt.lsts.accl.androidlib;


import pt.lsts.accl.bus.AcclBus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 *
 *
 * Created by jloureiro on 18-08-2015
 *
 */
public class AcclService extends Service {

    public static String TAG = "TAG";
    public IBinder binder;

    public AcclService(){
        TAG = this.getClass().getSimpleName();
        register();
    }

    @Override
    public IBinder onBind(Intent intent) {
        binder = new Binder();
        return binder;
    }

    public void start(Context context){
        Intent intent = new Intent(context, this.getClass());
        onStartCommand(intent, 0, 0);
        onBind(intent);
    }

    public void register(){
        AcclBus.register(this);
    }

    public void unregister(){
        AcclBus.unregister(this);
    }

}
