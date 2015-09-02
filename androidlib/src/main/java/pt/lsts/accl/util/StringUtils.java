package pt.lsts.accl.util;


import pt.lsts.accl.settings.Settings;

import java.util.Locale;

import android.util.Log;
import android.widget.VideoView;


/**
 *
 *
 * Created by jloureiro on 31-08-2015.
 */
public class StringUtils {

    public static final String TAG = StringUtils.class.getName();

    public static String arraytoString(String[] array){
        String result = "";
        for (String s : array){
            result += s;
        }
        return result;
    }

    public static String arraytoStringWithSeperator(String[] array, String separator){
        String result = array[0];
        for (int i=1;i<array.length;i++){
            result += separator;
            result += array[i];
        }
        return result;
    }

    public static String arraytoStringWithCommas(String[] array){
        return arraytoStringWithSeperator(array,",");
    }

    public static String arraytoStringWithSemicommas(String[] array){
        return arraytoStringWithSeperator(array,";");
    }

    public static String removeSysExtraInfo(String sysName){
        String res = sysName.split(" | ")[0];
        return res;
    }

    public static String timeSinceLastMessage(long t1, long t2){
        String string="Time since last message:\n";
        long hours=0;
        long minutes=0;
        long seconds = 0;
        long millisec = t1-t2;
        if (millisec>1000){
            seconds = millisec/1000;
            if (seconds>60){
                minutes = seconds/60;
                if (minutes>60){
                    hours = minutes/60;
                    minutes = minutes%60;
                }
                seconds = seconds%60;
            }
            millisec = millisec%1000;
        }
        if (hours>0){
            string += hours+"h ";
        }
        if (minutes>0){
            string += minutes+"m ";
        }
        if (seconds>0){
            string += seconds+"s ";
        }
        if (millisec>0){
            string += millisec+"ms";
        }

        return string;
    }

    public static boolean validateSetting(String setting){
        String parts[] = setting.split(",");
        if (parts.length<5)
            return false;
        String type = parts[0];
        switch (type){
            case "java.lang.String":
                break;
            case "java.lang.Integer":
                try{
                    int x = Integer.parseInt(parts[4]);
                    break;
                }catch (NumberFormatException e){
                    return false;
                }
            case "java.lang.Boolean":
                break;
            case "java.util.LinkedHashSet":
                break;
            default:
                return false;
        }

        return true;
    }

}
