package pt.lsts.accl.pos;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.location.LocationListener;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.util.AndroidUtil;


/**
 * Created by jloureiro on 09-09-2015.
 */
public class MyLocationListener implements SensorEventListener{

    private LocationManager locationManager = null;
    private SensorManager sensorManager = null;
    private Sensor pressureSensor = null;
    private Sensor accSensor = null;
    private Sensor magnetometerSensor = null;
    private float[] gravity;
    private float[] geomagnetic;

    /**
     * Create a Location Listener from a {@link Context}.
     *
     * @param context The context to create the location listener in, this context will be used to access device's sensors.
     */
    public MyLocationListener(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        initLocationListener();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Made Sensor Delay slower for lower battery consumption.
        // Position of the device is not critical to operation.
        // the Sensor delay can be readjusted to:
        // SensorManager.SENSOR_DELAY_NORMAL SensorManager.SENSOR_DELAY_GAME SensorManager.SENSOR_DELAY_FASTEST
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
        }

    /**
     * Initializa the location listener using both Network and GPS Providers.
     * Needs permission in Manifest: {@link android.permission#ACCESS_COARSE_LOCATION}
     */
    public void initLocationListener(){

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider and/or GPS.
                AcclBus.setPosition(AndroidUtil.calcPositionFromLocation(location));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Register the listener to be called upon changes.
     *
     * @param locationListener The listener to be registered.
     */
    public void registerListener(LocationListener locationListener){
        // In IDE these may display a warning of missing permission for:
        // ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION
        // These permission should be given by the developer on its own application.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /**
     * The required method to be called upon event from sensor.
     * Value is to be extracted depending on type of sensors from an array of values: {@link SensorEvent#values}
     * @param event the event occored.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE)
            AcclBus.setPressure(event.values[0]);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                AcclBus.setOrientation((float)Math.toDegrees(orientation[0])); // orientation contains: yaw/azimuth/phi, pitch/theta and roll/psi
            }
        }
    }

    /**
     * Required method to be Overrided, not used.
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

