package pt.lsts.accl.util;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.util.pos.EulerAngles;
import pt.lsts.accl.util.pos.LatLng;
import pt.lsts.accl.util.pos.Position;

/**
 *
 *
 * Created by jloureiro on 01-09-2015.
 */
public class AndroidUtil {

    /**
     * Switch from {@param currentActivityContext} to {@param destinyActivityClass}.
     *
     * @param currentActivityContext The Current Activity's context.
     * @param destinyActivityClass The static Class of destiny Activity.
     */
    public static void changeActivity(Context currentActivityContext, Class destinyActivityClass){
        Intent i = new Intent(currentActivityContext, destinyActivityClass);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivityContext.startActivity(i);
    }

    public static Position calcPositionFromLocation(Location location){
        double lat= location.getLatitude();
        double lon= location.getLongitude();
        LatLng latLngAbsolute = new LatLng(lat,lon);

        double orientation = AcclBus.getOrientation();

        double altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, AcclBus.getPressure());

        EulerAngles eulerAngles = new EulerAngles(0,0,orientation);

        // depth, z, height and euler angles are not applicable yet.
        return new Position(latLngAbsolute,orientation,-1,-1,-1,altitude,eulerAngles);

    }

}
