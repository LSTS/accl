package pt.lsts.accl.settings;


import pt.lsts.accl.androidlib.AcclSettingsActivity;

import android.util.Log;

import com.squareup.otto.Subscribe;


/**
 *
 *
 * Created by jloureiro on 03-09-2015.
 */
public class SettingsActivity extends AcclSettingsActivity{

    @Subscribe
    public void onString(String s){
        if (s.charAt(0)=='I')
            Log.i(TAG, s);
    }

}