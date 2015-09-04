package pt.lsts.accl.settings;


import pt.lsts.accl.androidlib.AcclFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 * Simple Fragment with some {@link ACCLSetting} settings of all different types.
 * These Fragment as part of the MainActivity launched with the Application will load all these settings on the startup of this app.
 *
 * Created by jloureiro on 03-09-2015.
 */
public class MainActivityFragment extends AcclFragment {

    @ACCLSetting(category = "myAnnotationCategory")
    String[] radioOptions = {"!default option", "option - 1", "option - 2", "option - 3", "option - 4"};

    @ACCLSetting(category = "different one", description = "some description")
    int integerSetting = 66;

    @ACCLSetting(category = "different one", description = "another description")
    String stringSetting = "string setting value";

    @ACCLSetting()
    boolean booleanSettingWithoutNoAnnotationArgs = false;

    public MainActivityFragment() {
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
