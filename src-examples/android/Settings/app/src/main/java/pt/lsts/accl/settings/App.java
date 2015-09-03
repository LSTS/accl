package pt.lsts.accl.settings;


import android.app.Application;


/**
 *
 *
 * Created by jloureiro on 03-09-2015.
 */
public class App extends Application {

    public static final String TAG = "App";

    @Override
    public void onCreate(){
        super.onCreate();
        Settings.initSettings(getBaseContext());
    }
}
