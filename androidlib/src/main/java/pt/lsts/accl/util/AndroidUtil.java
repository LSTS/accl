package pt.lsts.accl.util;

import android.content.Context;
import android.content.Intent;

/**
 *
 *
 * Created by jloureiro on 01-09-2015.
 */
public class AndroidUtil {

    public static void changeActivity(Context currentActivityContext, Class destinyActivityClass){
        Intent i = new Intent(currentActivityContext, destinyActivityClass);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivityContext.startActivity(i);
    }
}
