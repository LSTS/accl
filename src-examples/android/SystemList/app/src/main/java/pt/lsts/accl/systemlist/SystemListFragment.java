package pt.lsts.accl.systemlist;


import pt.lsts.accl.android.AcclFragment;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventSystemDisconnected;
import pt.lsts.accl.event.EventSystemVisible;
import pt.lsts.imc.IMCMessage;

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

    /**
     * Register this as a listenners for {@link AcclBus} messages and Events.
     */
    public SystemListFragment() {
        AcclBus.register(this);
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(inflater, container, R.layout.fragment_system_list);
    }

    /**
     * Initialize the List View.
     */
    @Override
    public void onResume(){
        super.onResume();

        initListView();
        //showToastShort(TAG+": finished onResume");
    }

    /**
     * Find the List View and create an {@link ArrayAdapter<String>}.
     */
    public void initListView(){
        systemListListView = (ListView) view.findViewById(R.id.systemListListView);

        if (systemListListView!=null){
            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
            systemListListView.setAdapter(arrayAdapter);
        }else{
            Log.e(TAG, "SystemListView failed to be initialized");
        }
    }

    /**
     * Listener for generic {@link IMCMessage}.
     *
     * @param msg The {@link IMCMessage} received by the system.
     */
    @Subscribe
    public void onMsg(IMCMessage msg){
        Log.v(TAG, "received msg: "+msg.getMessageType());
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

    /**
     * Remove a system from the list upon an {@link EventSystemDisconnected}.
     *
     * @param event The event containing the System that has been disconnected.
     */
    @Subscribe
    public void on(EventSystemDisconnected event) {
        Log.v(TAG, event.toString());
        showToastLong(event.getSys().getName()+" Disconnected!");

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
