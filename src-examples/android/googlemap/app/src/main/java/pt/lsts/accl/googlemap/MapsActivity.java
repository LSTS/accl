package pt.lsts.accl.googlemap;


import pt.lsts.accl.android.AcclActivity;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.sys.Sys;

import java.util.Vector;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by jloureiro on 15-09-2015.
 */
public class MapsActivity extends AcclActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /**
     *
     * The Vector containing the markers added to #mMap.
     *
     * These can be updated and/or removed as needed and triggered by events:
     * {@link pt.lsts.accl.event.EventSystemDisconnected} {@link pt.lsts.accl.event.EventSystemUpdated} and {@link pt.lsts.accl.event.EventSystemDisconnected}.
     */
    private Vector<Marker> markers = new Vector<Marker>();

    //AcclService responsible for receiving IMCMessages and AcclBus events
    private MessageReceiverService messageReceiverService;

    /**
     * Setup Map and Button.
     *
     * @param savedInstanceState The SavedInstance of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        setUpButton();
    }

    /**
     *
     * Setup map if needed, start the {@link MessageReceiverService}.
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mMap!=null && mMap.getMyLocation()!=null)
            Log.v(TAG, "myPos:\n" + mMap.getMyLocation().toString());
        messageReceiverService = new MessageReceiverService(this);
    }

    /**
     * Update the marker representing the {@param sys}
     * This method should be triggered by {@link pt.lsts.accl.event.EventSystemUpdated}.
     *
     * @param sys The System which marker is to be updated
     */
    public void updateMarker(Sys sys){
        for (Marker marker : markers){
            if (marker.getId()==sys.getName()){
                final Marker markerFinal = marker;
                final Sys sysFinal = sys;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        markerFinal.setPosition(new LatLng(sysFinal.getPosition().getLatitude(), sysFinal.getPosition().getLongitude()));
                        markerFinal.setRotation((float) sysFinal.getPosition().getOrientation());
                    }
                });
                return;
            }
        }
        addMarker(sys);
    }

    /**
     * Remove the marker representing the {@param sys}.
     * This method should be triggered by {@link pt.lsts.accl.event.EventSystemDisconnected}.
     *
     * @param sys The System which marker is to be removed
     */
    public void removeMarker(Sys sys){
        for (Marker marker : markers) {
            if (marker.getId() == sys.getName()) {
                marker.remove();
            }
        }
    }

    /**
     * Add a new marker to represent {@param sys}.
     * This method is triggered by {@link #updateMarker(Sys)} when the {@param sys} doesn't have a marker yet.
     *
     * @param sys The System represented by the new marker.
     */
    public void addMarker(final Sys sys){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Marker marker = mMap.addMarker(
                        new MarkerOptions()
                                .title(sys.getName())
                                .position(new LatLng(sys.getPosition().getLatitude(), sys.getPosition().getLongitude()))
                                .rotation((float) sys.getPosition().getOrientation())
                );
                Log.v(TAG, "added Marker:\n" + marker.toString());
                markers.add(marker);
            }
        });
    }

    /**
     * Check if a system has a marker representing it yet.
     *
     * @param name The name of the system.
     * @return true if the sys has a marker already, false otherwise.
     */
    public boolean doesSysHaveMarker(String name){
        for (Marker marker : markers)
            if (marker.getId().equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    /**
     * Setup the button to show position.
     *
     * @see AcclBus#getPosition()
     */
    public void setUpButton(){
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AcclBus.getPosition() != null) {
                    showToastLong(AcclBus.getPosition().toString());
                } else {
                    showToastLong("AcclBus.getPosition()==null");
                }
            }
        });
    }

}
