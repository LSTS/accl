package pt.lsts.accl.pos;


import pt.lsts.accl.bus.AcclBus;
import pt.lsts.imc.EstimatedState;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;


/**
 * Created by jloureiro on 11-09-2015.
 */
public class MyLocationDisseminator {

    private static boolean active = false;
    private static Timer timer = new Timer(true);

    /**
     * TimerTask for periodically broadcast {@link EstimatedState} message with device's:
     * Position (GPS), Orientation (Acc/Mag) and Altitude (Pressure).
     *
     * This values are given by android's sensors:
     * Pressure Sensor: {@link android.hardware.Sensor#TYPE_PRESSURE}
     * Accelerometer Sensor: {@link android.hardware.Sensor#TYPE_ACCELEROMETER);
     * Magnetic Field Sensor: {@link android.hardware.Sensor#TYPE_MAGNETIC_FIELD);
     *
     * @see android.hardware.Sensor
     *
     */
    private static TimerTask sendEstimatedState = new TimerTask() {
        @Override
        public void run() {
            if (active==false) {
                timer.cancel();
                return;
            }
            EstimatedState estimatedState = new EstimatedState();

            if (AcclBus.getPosition()==null)
                return;
            estimatedState.setLat(Math.toRadians(AcclBus.getPosition().getLatitude()));
            estimatedState.setLon(Math.toRadians(AcclBus.getPosition().getLongitude()));

            estimatedState.setAlt(AcclBus.getPosition().getAltitude());
            estimatedState.setPsi(Math.toRadians(AcclBus.getPosition().getOrientation()));

            AcclBus.sendBroadcastMessage(estimatedState);
        }
    };

    /**
     * Deactivate the sending of EstimatedState messages.
     */
    public static void deactivate(){
        active=false;
    }

    /**
     * Start disseminating {@link EstimatedState} messages.
     *
     * With standard period of 1000ms = 1 second.
     */
    public static void startMyLocationDisseminator(){
        active=true;
        if (timer!=null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(sendEstimatedState, 1000, 1000);
    }

    /**
     * Start disseminating {@link EstimatedState} messages.
     *
     * @param intervalMilliSeconds The interval in milliseconds.
     */
    public static void startMyLocationDisseminator(int intervalMilliSeconds){
        active=true;
        if (timer!=null){
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(sendEstimatedState, intervalMilliSeconds, intervalMilliSeconds);
    }

    /**
     * Start disseminating {@link EstimatedState} messages.
     *
     * @param delayMilliseconds The initial delay in milliseconds
     * @param intervalMilliSeconds The interval in milliseconds
     */
    public static void startMyLocationDisseminator(int delayMilliseconds, int intervalMilliSeconds){
        active=true;
        if (timer!=null){
            timer.cancel();
        }else{
            timer = new Timer(true);
        }
        timer.scheduleAtFixedRate(sendEstimatedState, delayMilliseconds, intervalMilliSeconds);
    }


}
