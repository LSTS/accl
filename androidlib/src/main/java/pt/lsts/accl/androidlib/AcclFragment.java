package pt.lsts.accl.androidlib;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 * AcclFragment to be extended by user.
 * Contains call's to its activity listenners and toasts methods.
 * Stores the View for search of subViews.
 *
 * Created by jloureiro on 12-08-2015.
 *
 */
public class AcclFragment extends Fragment {

    public static String TAG = "TAG";
    public View view;//View to be used to search for subviews.

    /**
     *
     * Standard empty Constructor.
     *
     */
    public AcclFragment() {
        TAG = this.getClass().getSimpleName();
    }

    /**
     *
     * Android's standard onCreateView override.
     * Please refer to {@link android.support.v4.app.Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(inflater, container, R.layout.fragment_accl);
    }

    /**
     *
     * Android's standard onResume override.
     * Please refer to {@link Fragment#onResume()}
     *
     */
    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     *
     * Android's standard onPause override
     * Please refer to {@link Fragment#onPause()}
     *
     */
    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Create a view from a dinamic fragmentID.
     *
     * @param inflater Please refer to {@link #onCreateView(LayoutInflater, ViewGroup, int)}
     * @param container Please refer to {@link #onCreateView(LayoutInflater, ViewGroup, int)}
     * @param fragmentID The Fragment ID from R file to generate the view from.
     * @return The generated View.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, int fragmentID){
        view = inflater.inflate(fragmentID, container, false);
        return view;
    }

    /**
     *
     * Register a listenner with AcclBus using this Fragment's Activity register method.
     * Please refer to {@link pt.lsts.accl.androidlib.AcclActivity#register(Object)}
     *
     * @param pojo The Listenner to be registed.
     *
     */
    public void register(Object pojo) {
        AcclActivity acclActivity = (AcclActivity) getActivity();
        acclActivity.register(pojo);
    }

    /**
     *
     * Unregister a listenner with AcclBus using this Fragment's Activity unregister method.
     * Please refer to {@link pt.lsts.accl.androidlib.AcclActivity#unregister(Object)}
     *
     * @param pojo The Listenner to be unregisted.
     *
     */
    public synchronized void unregister(Object pojo) {
        AcclActivity acclActivity = (AcclActivity) getActivity();
        acclActivity.unregister(pojo);
    }

    /**
     *
     * Unregister all listenners with AcclBus using this Fragment's Activity unregisterAll method.
     * Please refer to {@link pt.lsts.accl.androidlib.AcclActivity#unregisterAll()}
     *
     */
    public void unregisterAll(){
        AcclActivity acclActivity = (AcclActivity) getActivity();
        acclActivity.unregisterAll();
    }

    /**
     * Show a message for a short time via a Toast using this Fragment's Activity showToastShort method.
     * Please refer to {@link pt.lsts.accl.androidlib.AcclActivity#showToastShort(String)}
     * Please refer to {@link android.widget.Toast}
     *
     * @param msg The Message to be shown.
     *
     */
    public void showToastShort(String msg){
        AcclActivity acclActivity = ((AcclActivity) getActivity());
        acclActivity.showToastShort(msg);
    }

    /**
     * Show a long message for a long time via a Toast using this Fragment's Activity showToastLong method.
     * Please refer to {@link pt.lsts.accl.androidlib.AcclActivity#showToastLong(String)}
     * Please refer to {@link android.widget.Toast}
     *
     * @param msg The Message to be shown.
     *
     */
    public void showToastLong(String msg){
        AcclActivity acclActivity = ((AcclActivity) getActivity());
        acclActivity.showToastLong(msg);
    }


}
