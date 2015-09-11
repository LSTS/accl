package pt.lsts.accl.pos;

import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.imc.EstimatedState;

/**
 * Created by jloureiro on 11-09-2015.
 */
public class MyLocationDisseminator {

    private static boolean active = false;
    private static Timer timer = new Timer(true);

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

    public static void deactivate(){
        active=false;
    }

    public static void startMyLocationDisseminator(){
        active=true;
        if (timer!=null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(sendEstimatedState, 1000, 1000);
    }

    public static void startMyLocationDisseminator(int intervalMilliSeconds){
        active=true;
        if (timer!=null){
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(sendEstimatedState, intervalMilliSeconds, intervalMilliSeconds);
    }

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
