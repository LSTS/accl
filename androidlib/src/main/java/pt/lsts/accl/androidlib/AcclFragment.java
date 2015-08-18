package pt.lsts.accl.androidlib;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 * A placeholder fragment containing a simple view.
 *
 *
 * Created by jloureiro on 12-08-2015.
 *
 */
public class AcclFragment extends Fragment {

    public static String TAG = "TAG";
    public View view;//View to be used to search for subviews.

    public AcclFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(inflater, container, R.layout.fragment_accl);
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
        view = inflater.inflate(fragmentID, container, false);
        return view;
    }

    public void showToastShort(String msg){
        AcclActivity acclActivity = ((AcclActivity) getActivity());
        acclActivity.showToastShort(msg);
    }

    public void showToastLong(String msg){
        AcclActivity acclActivity = ((AcclActivity) getActivity());
        acclActivity.showToastLong(msg);
    }


}
