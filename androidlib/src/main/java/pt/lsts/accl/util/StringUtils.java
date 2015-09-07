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

    /**
     * Convert an array of string into a single String
     * @param array The array of string to be converted.
     * @return The resulting string of concatenation of all string in {@param array}
     */
    public static String arraytoString(String[] array){
        String result = "";
        for (String s : array){
            result += s;
        }
        return result;
    }

    /**
     * Convert an array of string into a single string separating elements by a given separator.
     *
     * @param array The array of string to be converted.
     * @param separator The separator between elements.
     * @return
     */
    public static String arraytoStringWithSeperator(String[] array, String separator){
        String result = array[0];
        for (int i=1;i<array.length;i++){
            result += separator;
            result += array[i];
        }
        return result;
    }

    /**
     * Use {@link #arraytoStringWithSeperator(String[], String)} with separator ",".
     *
     * @param array The array to be passed as arg.
     * @return The result of concateanting elements in {@param array} with separator ",".
     */
    public static String arraytoStringWithCommas(String[] array){
        return arraytoStringWithSeperator(array,",");
    }

    /**
     * Use {@link #arraytoStringWithSeperator(String[], String)} with separator ";".
     *
     * @param array The array to be passed as arg.
     * @return The result of concateanting elements in {@param array} with separator ";".
     */
    public static String arraytoStringWithSemicommas(String[] array){
        return arraytoStringWithSeperator(array,";");
    }

    /**
     * Remove the Extra info of a System from a String
     *
     * @param sysInfo The full string with Sys info.
     * @return The stripped String with only the sysName.
     */
    public static String removeSysExtraInfo(String sysInfo){
        String res = sysInfo.split(" | ")[0];
        return res;
    }

    /**
     * Convert a string with elements sepperated by commas into an Array.
     *
     * @param str The Full string in format: "elem1,elem2,elem3,..."
     * @return The array of strings in format: {"elem1", "elem2", "elem3", ...}
     */
    public static String[] stringWithCommastoArray(String str){
        return str.split(",");
    }

    /**
     * Provide a String describing how much time since last message up to hours.
     *
     * @param t1 The Current Time.
     * @param t2 The Time of last message
     * @return A descriptive string of time since last message in format: "11h 11m 11s 111ms"
     */
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

    /**
     * Validate a string setting.
     *
     * @param setting The Setting.
     * @return true if setting is valid, false otherwise.
     */
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
