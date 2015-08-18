package pt.lsts.accl.systemlist;

import pt.lsts.accl.androidlib.AcclFragment;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.squareup.otto.Subscribe;


/**
 * A placeholder fragment containing a simple view.
 */
public class SystemListFragment extends AcclFragment {


    ListView systemListListView=null;
    ArrayAdapter<String> arrayAdapter;

    public SystemListFragment() {
        AcclBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(inflater, container, R.layout.fragment_system_list);
    }

    @Override
    public void onResume(){
        super.onResume();

        initListView();
        //showToastShort(TAG+": finished onResume");
    }

    public void initListView(){
        systemListListView = (ListView) view.findViewById(R.id.systemListListView);

        if (systemListListView!=null){
            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
            systemListListView.setAdapter(arrayAdapter);
        }else{
            Log.e(TAG, "SystemListView failed to be initialized");
        }
    }

    @Subscribe
    public void on(EventSystemVisible event) {
        Log.v(TAG, event.toString());

        final EventSystemVisible eventFinal = event;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (arrayAdapter!=null)
                    arrayAdapter.add(eventFinal.getSys().getName());
            }
        });
    }

    @Subscribe
    public void on(EventSystemDisconnected event) {
        Log.v(TAG, event.toString());

        final EventSystemDisconnected eventFinal = event;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (eventFinal != null && eventFinal.getSys() != null && eventFinal.getSys().getName() != null)
                    arrayAdapter.remove(eventFinal.getSys().getName());
                else
                    Log.e(TAG, "eventFinal!=null && eventFinal.getSys()!=null && eventFinal.getSys().getName()!=null");
            }
        });
    }


}
