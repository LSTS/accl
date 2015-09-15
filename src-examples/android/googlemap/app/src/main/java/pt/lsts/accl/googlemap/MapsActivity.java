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
    private Vector<Marker> markers = new Vector<Marker>();

    //AcclService responsible for receiving IMCMessages and AcclBus events
    private MessageReceiverService messageReceiverService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        setUpButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mMap!=null && mMap.getMyLocation()!=null)
            Log.v(TAG, "myPos:\n" + mMap.getMyLocation().toString());
        messageReceiverService = new MessageReceiverService(this);
    }

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

    public void removeMarker(Sys sys){
        for (Marker marker : markers) {
            if (marker.getId() == sys.getName()) {
                marker.remove();
            }
        }
    }

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
