package pt.lsts.accl.googlemap;


import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.pos.MyLocationDisseminator;
import pt.lsts.accl.pos.MyLocationListener;

import android.app.Application;


/**
 * Created by jloureiro on 09-09-2015.
 */
public class App extends Application {
    public static final String TAG = "App";

    private MyLocationListener myLocationListener;

    /**
     * Bind the AcclBus.
     * @see AcclBus
     *
     * Create and start the {@link MyLocationDisseminator}.
     *
     */
    @Override
    public void onCreate(){
        super.onCreate();

        AcclBus.bind("Googlemap", 6006);

        //start myLocationListenner to have:
        // GPS position
        // altitude from pressure
        // orientation from accelerometer and geomagnetic sensors
        myLocationListener = new MyLocationListener(this);

        // start disseminating this location, altitude and orientation with EstimatedState messages
        MyLocationDisseminator.startMyLocationDisseminator();
    }

}
