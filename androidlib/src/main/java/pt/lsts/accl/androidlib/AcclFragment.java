package pt.lsts.accl.androidlib;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class AcclFragment extends Fragment {

    public static String TAG = "TAG";
    public View v;

    public AcclFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(inflater,container, R.layout.fragment_accl);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, int fragmentID){
        return inflater.inflate(fragmentID, container, false);
    }

}
