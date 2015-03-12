package pt.lsts.accl.settings;

import pt.lsts.accl.ACCL;

import java.util.Map;

import android.content.SharedPreferences;
import android.util.Log;

//import com.squareup.otto.Bus;

public class Settings {

    private final static String TAG = "Settings";
	private static Settings settings = null;

	private Settings() {

	}

	public static Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	public static SharedPreferences getSharedPreferences() {
		return ACCL.getInstance().sharedPreferences;
	}

    public static boolean putFullString(String key, String value) {
        boolean result = ACCL.getInstance().sharedPreferences.edit().putString(key, value).commit();
        //ACCL.getInstance().getBus().post(key);
        Log.v(TAG, "ACCL.getInstance().getBus().post("+key+");");
        return result;
    }

	public static boolean putString(String key, String value) {
        String finalValueString = getType(key,"java.lang.String");
        finalValueString += ",";
        finalValueString += getCategory(key,"null");
        finalValueString += ",";
        finalValueString += getKey(key,"null");
        finalValueString += ",";
        finalValueString += getDescription(key,"null");
        finalValueString += ",";
        finalValueString += value;
        boolean result = ACCL.getInstance().sharedPreferences.edit().putString(key, finalValueString).commit();
        ACCL.getInstance().getBus().post(key);
        Log.v(TAG, "ACCL.getInstance().getBus().post("+key+");");
		return result;
	}

	public static boolean putInt(String key, int value) {
        String finalValueString = getType(key,"java.lang.String");
        finalValueString += ",";
        finalValueString += getCategory(key,"null");
        finalValueString += ",";
        finalValueString += getKey(key,"null");
        finalValueString += ",";
        finalValueString += getDescription(key,"null");
        finalValueString += ",";
        finalValueString += value;
        boolean result = ACCL.getInstance().sharedPreferences.edit().putString(key, finalValueString).commit();
        ACCL.getInstance().getBus().post(key);
        Log.v(TAG, "ACCL.getInstance().getBus().post("+key+");");
        return result;
	}

	public static boolean putBoolean(String key, boolean value) {
        String finalValueString = getType(key,"java.lang.String");
        finalValueString += ",";
        finalValueString += getCategory(key,"null");
        finalValueString += ",";
        finalValueString += getKey(key,"null");
        finalValueString += ",";
        finalValueString += getDescription(key,"null");
        finalValueString += ",";
        finalValueString += value;
        boolean result = ACCL.getInstance().sharedPreferences.edit().putString(key, finalValueString).commit();
        ACCL.getInstance().getBus().post(key);
        Log.v(TAG, "ACCL.getInstance().getBus().post("+key+");");
        return result;
	}

	public static boolean clear() {
		return ACCL.getInstance().sharedPreferences.edit().clear().commit();
	}

	public static Map<String, ?> getAll() {
		return ACCL.getInstance().sharedPreferences.getAll();
	}

    public static boolean isOptions(String key){
        if (key.endsWith("_options")==false)
            return false;
        if (exists(key)==false)
            return false;
        return true;
    }

    public static boolean hasOptions(String key){
        String keyOptions = key + "_options";
        if (exists(keyOptions))
            return true;
        return false;
    }

    public static String[] getOptions(String key){
        String[] result = new String[0];
        if (hasOptions(key)==true){
            key += "_options";
            result = getStrings(key, new String[0]);
        }
        return result;
    }

    public static boolean exists(String key){
        return ACCL.getInstance().sharedPreferences.contains(key);
    }

    public static String getType(String key, String defValue){
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// get Type
            return ACCL.getInstance().sharedPreferences.getString(key,"null").split(",")[0];
        }
        return defValue;
    }

    public static String getCategory(String key, String defValue) {
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// get only Category
            Log.i(TAG,key+" .getString.toString= "+ACCL.getInstance().sharedPreferences.getString(key,"null").toString());
            return ACCL.getInstance().sharedPreferences.getString(key,"null").split(",")[1];
        }
        return defValue;
    }

    public static String getKey(String key, String defValue) {
		if (ACCL.getInstance().sharedPreferences.contains(key)) {// get key of setting
            return ACCL.getInstance().sharedPreferences.getString(key,"null").split(",")[2];
		}
		return defValue;
	}

    public static String getDescription(String key, String defValue){
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// get description
            return ACCL.getInstance().sharedPreferences.getString(key,"null").split(",")[3];
        }
        return defValue;
    }


    public static String getString(String key, String defValue) {
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// remove Category
            String valueString = ACCL.getInstance().sharedPreferences.getString(key,String.valueOf(defValue));
            return valueString.split(",")[4];
        }
        return defValue;
    }

    public static String[] getStrings(String key, String[] defValue){
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// remove Category
            String valueString = ACCL.getInstance().sharedPreferences.getString(key,String.valueOf(defValue));
            String[] result = new String[valueString.split(",").length-4];
            for (int i=4;i<valueString.split(",").length;i++){
                result[i-4] = valueString.split(",")[i];
            }
            return result;
        }
        return defValue;
    }

    public static int getInt(String key, int defValue) {
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// remove Category
            String valueString = ACCL.getInstance().sharedPreferences.getString(key,String.valueOf(defValue));
            return Integer.parseInt(valueString.split(",")[4]);
        }
        return defValue;
    }

    public static boolean getBoolean(String key, boolean defValue) {
        if (ACCL.getInstance().sharedPreferences.contains(key)) {// remove Category
            String valueString = ACCL.getInstance().sharedPreferences.getString(key,String.valueOf(defValue));
            return Boolean.parseBoolean(valueString.split(",")[4]);
        }
        return defValue;
    }

}
