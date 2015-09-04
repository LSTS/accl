package pt.lsts.accl.settings;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.util.FileOperations;
import pt.lsts.accl.util.StringUtils;

import static pt.lsts.accl.settings.ACCLSetting.*;


/**
 *
 *
 * Created by jloureiro on 01-09-2015.
 */
public class Settings {

    private static String TAG = "TAG";
	private static Settings settings = null;
    private static SharedPreferences sharedPreferences=null;

    /**
     *
     * Private constructor to ensure the Singleton Pattern.
     */
	private Settings() {
        TAG = this.getClass().getSimpleName();

	}

    /**
     * Public Singleton constructor returning always the same only instance of this class.
     *
     * @return The singleton obj of this class.
     */
	public static Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

    /**
     * Get The Android's {@link SharedPreferences}, should be used and changed with care.
     *
     * @return The Singleton {@link SharedPreferences} instance.
     */
	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

    /**
     * Put a Full String of setting on the {@link SharedPreferences}.
     * Used only once per setting return false and posts an Error msg if tryed to place with repeated key.
     * The default behavior after parsing a Setting line.
     *
     * @param key The key of the setting.
     * @param value The Full line of Setting to be stored and parsed later.
     * @return false if an error occurs or setting already there, true otherwise.
     */
    public static boolean putFullString(String key, String value) {
        if (sharedPreferences.contains(key)){
            AcclBus.post("ERROR - "+"Setting with key:\""+key+"\" already registered.");
            return false;
        }
        boolean result = sharedPreferences.edit().putString(key, value).commit();
        return result;
    }

    /**
     * Put a new/Update an String Setting.
     *
     * @param key The Setting key.
     * @param value The new String value.
     * @return false if an error occurs, true otherwise.
     */
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
        boolean result = sharedPreferences.edit().putString(key, finalValueString).commit();
		return result;
	}

    /**
     * Put a new/Update an Integer Setting.
     *
     * @param key The Setting key.
     * @param value The new Integer value.
     * @return false if an error occurs, true otherwise.
     */
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
        boolean result = sharedPreferences.edit().putString(key, finalValueString).commit();
        return result;
	}

    /**
     * Put a new/Update an Boolean Setting.
     *
     * @param key The Setting key.
     * @param value The new Boolean value.
     * @return false if an error occurs, true otherwise.
     */
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
        boolean result = sharedPreferences.edit().putString(key, finalValueString).commit();
        return result;
	}

    /**
     * Clear all Settings.
     *
     * @return false if an error occurs, true otherwise.
     */
	public static boolean clear() {
		return sharedPreferences.edit().clear().commit();
	}

    /**
     *
     * @see SharedPreferences#getAll()
     */
	public static Map<String, ?> getAll() {
		return sharedPreferences.getAll();
	}

    /**
     * Method to be called by an extension of {@link android.app.Application} to copy default_settings.csv file to Android device
     * Init settings with default values from this file if no Settings are previously activated.
     *
     * @param context {@link Application#getBaseContext()}
     */
    public static void initSettings(Context context) {
        FileOperations.copyAssetsFolder(context, "");//"" root folder
        Settings.getSettings();
        if (Settings.sharedPreferences==null)
            Settings.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (Settings.getAll().isEmpty()) {// if no previous settings, set the defaults
            Profile.restoreDefaults();
        }
    }

    /**
     * Is the setting with this key a set of options for a String setting.
     *
     * @param key The Setting key.
     * @return true if setting key ends with _options, false otherwise.
     */
    public static boolean isOptions(String key){
        if (key.endsWith("_options")==false)
            return false;
        if (exists(key)==false)
            return false;
        return true;
    }

    /**
     * Does the setting with this key have a set of options.
     *
     * @param key The Setting key.
     * @return true if there is another setting with a set of options for the setting with the {@param key}, false otherwise.
     */
    public static boolean hasOptions(String key){
        String keyOptions = key + "_options";
        if (exists(keyOptions))
            return true;
        return false;
    }

    /**
     * Get the set of options for this Setting.
     *
     * @param key The Setting key.
     * @return An array of options. If there is not one, an empty array of Strings will be returned instead.
     */
    public static String[] getOptions(String key){
        String[] result = new String[0];
        if (hasOptions(key)==true){
            key += "_options";
            result = getStrings(key, new String[0]);
        }
        return result;
    }

    /**
     * Does a setting with this key exist?
     *
     * @param key The Setting key.
     * @return true if a setting with {@param key} exists, false otherwise.
     */
    public static boolean exists(String key){
        return sharedPreferences.contains(key);
    }

    /**
     * Get the type of the setting with {@param key}.
     *
     * @param key The Setting key.
     * @param defValue The default value to be returned if no setting exists.
     * @return A string of the type of the setting with {@param key}.
     */
    public static String getType(String key, String defValue){
        if (sharedPreferences.contains(key)) {// get Type
            return sharedPreferences.getString(key,"null").split(",")[0];
        }
        return defValue;
    }

    /**
     * Get the category of the Setting with {@param key}.
     *
     * @param key The Setting key.
     * @param defValue The default value to be returned if no setting exists.
     * @return The category of the setting with {@param key}.
     */
    public static String getCategory(String key, String defValue) {
        if (sharedPreferences.contains(key)) {// get only Category
            Log.i(TAG, key + " .getString.toString= " + sharedPreferences.getString(key, "null").toString());
            return sharedPreferences.getString(key,"null").split(",")[1];
        }
        return defValue;
    }

    /**
     * Get the key of a setting.
     * Using a defValue different from key, this method can be used to find if such a setting exists.
     *
     * @param key The Setting key.
     * @param defValue The default value to be returned if no such setting exists.
     * @return The {@param key} if the setting exists, {@param defValue} otherwise.
     */
    public static String getKey(String key, String defValue) {
		if (sharedPreferences.contains(key)) {// get key of setting
            return sharedPreferences.getString(key,"null").split(",")[2];
		}
		return defValue;
	}

    /**
     * Get the description of the Setting with {@param key}.
     *
     * @param key The Setting key.
     * @param defValue The default value to be returned if no setting exists.
     * @return The description of the setting with {@param key}.
     */
    public static String getDescription(String key, String defValue){
        if (sharedPreferences.contains(key)) {// get description
            return sharedPreferences.getString(key,"null").split(",")[3];
        }
        return defValue;
    }

    /**
     * Get a String Setting.
     *
     * @param key The Setting key.
     * @param defValue The default value.
     * @return The current value of this setting if it exists, {@param key} otherwise.
     */
    public static String getString(String key, String defValue) {
        if (sharedPreferences.contains(key)) {// remove Category
            String valueString = sharedPreferences.getString(key,String.valueOf(defValue));
            return valueString.split(",")[4];
        }
        return defValue;
    }

    /**
     * Get a set of Strings for a Setting with options.
     *
     * @param key The Setting key.
     * @param defValue The default value.
     * @return The set of options for the setting with {@param key}.
     */
    public static String[] getStrings(String key, String[] defValue){
        if (sharedPreferences.contains(key)) {
            String valueString = sharedPreferences.getString(key,String.valueOf(defValue));
            String[] result = new String[valueString.split(",").length-4];
            for (int i=4;i<valueString.split(",").length;i++){
                result[i-4] = valueString.split(",")[i];
            }
            return result;
        }
        return defValue;
    }

    /**
     * Get a Integer Setting.
     *
     * @param key The Setting key.
     * @param defValue The default value.
     * @return The current value of this setting if it exists, {@param key} otherwise.
     */
    public static int getInt(String key, int defValue) {
        if (sharedPreferences.contains(key)) {// remove Category
            String valueString = sharedPreferences.getString(key,String.valueOf(defValue));
            return Integer.parseInt(valueString.split(",")[4]);
        }
        return defValue;
    }

    /**
     * Get a Boolean Setting.
     *
     * @param key The Setting key.
     * @param defValue The default value.
     * @return The current value of this setting if it exists, {@param key} otherwise.
     */
    public static boolean getBoolean(String key, boolean defValue) {
        if (sharedPreferences.contains(key)) {// remove Category
            String valueString = sharedPreferences.getString(key,String.valueOf(defValue));
            return Boolean.parseBoolean(valueString.split(",")[4]);
        }
        return defValue;
    }

    /**
     * Register ALL {@link ACCLSetting} from the {@param obj}.
     *
     * @param obj The Object to get the {@link ACCLSetting} from.
     * @return false if the registering of settings occurred in errors, true otherwise.
     */
    public static boolean registerACCLSettingsAnnotationsFromClass(Object obj){
        boolean result=true;
        Class c = obj.getClass();
        for (Field field : c.getDeclaredFields()){
            if (field.isAnnotationPresent(ACCLSetting.class)){
                Annotation annotation = field.getAnnotation(ACCLSetting.class);
                ACCLSetting acclSetting = (ACCLSetting) annotation;
                boolean bool = registerACCLSettingsAnnotation(field, obj, acclSetting);
                if (bool==false)
                    result=false;
            }
        }
        return result;
    }

    /**
     * Register a single {@link ACCLSetting}.
     *
     * @param field The field asssociated with the {@link ACCLSetting}.
     * @param obj The object that has the {@link ACCLSetting}.
     * @param acclSetting The {@link ACCLSetting} itself.
     * @return false if errors occur registering the setting, true otherwise.
     */
    public static boolean registerACCLSettingsAnnotation(Field field, Object obj, ACCLSetting acclSetting){


        AcclBus.post("INFO - "+"acclSetting.toString():\n"+acclSetting.toString());
        try {

            if (field.getGenericType() == int.class) {
                String setting="";//final string with whole line of setting
                setting += "java.lang.Integer";
                setting += ",";
                setting += acclSetting.category();
                setting += ",";
                setting += field.getName();
                setting += ",";
                setting += acclSetting.description();
                setting += ",";
                setting += field.getInt(obj);

                return Profile.loadSetting(setting);
            }
            if (field.getGenericType() == boolean.class) {
                String setting="";//final string with whole line of setting
                setting += "java.lang.Boolean";
                setting += ",";
                setting += acclSetting.category();
                setting += ",";
                setting += field.getName();
                setting += ",";
                setting += acclSetting.description();
                setting += ",";
                setting += field.getBoolean(obj);

                return Profile.loadSetting(setting);
            }
            if (field.getGenericType() == String[].class) {
                String setting="";//final string with whole line of setting
                String type = "java.lang.String";
                String cat = acclSetting.category();
                String key = field.getName();
                String description = acclSetting.description();
                String value = ((String[]) field.get(obj))[0];

                setting = "";
                setting += type;
                setting += ",";
                setting += cat;
                setting += ",";
                setting += key;
                setting += ",";
                setting += description;
                setting += ",";
                setting += value;

                boolean result = true;
                boolean bool = Profile.loadSetting(setting);
                if (bool == false)
                    result = false;

                key += "_options";
                value = StringUtils.arraytoStringWithCommas((String[])field.get(obj));

                setting = "";
                setting += type;
                setting += ",";
                setting += cat;
                setting += ",";
                setting += key;
                setting += ",";
                setting += description;
                setting += ",";
                setting += value;

                bool = Profile.loadSetting(setting);
                if (bool == false)
                    result = false;
                return result;
            }
            //else treat it as a string:
            String setting="";//final string with whole line of setting
            setting += "java.lang.String";
            setting += ",";
            setting += acclSetting.category();
            setting += ",";
            setting += field.getName();
            setting += ",";
            setting += acclSetting.description();
            setting += ",";
            setting += field.get(obj).toString();

            return Profile.loadSetting(setting);

        }catch(Exception e){
            AcclBus.post("ERROR - "+"register @ACCLSetting Exception:\n"+e.getMessage());
            return false;
        }
    }

    /**
     * Update ALL Annotation's associated field from an Object.
     *
     * @param obj The Object to have Annotation's associated fields updated.
     * @return false if any of the Annotation has any errors, true otherwise.
     */
    public static boolean updateAllAnnotations(Object obj){
        boolean result=true;
        for (Field field : obj.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(ACCLSetting.class)){
                Annotation annotation = field.getAnnotation(ACCLSetting.class);
                ACCLSetting acclSetting = (ACCLSetting) annotation;
                boolean bool = updateAnnotation(obj, field, acclSetting);
                if (bool==false)
                    result=false;
            }
        }
        return result;
    }

    /**
     * Update a single {@link ACCLSetting}.
     *
     * @param obj The Object to have Annotation's associated fields updated.
     * @param field The associated field.
     * @param acclSetting The {@link ACCLSetting} itself.
     * @return false if errors occur, true otherwise.
     */
    public static boolean updateAnnotation(Object obj, Field field, ACCLSetting acclSetting){
        try{
            if(field.getType()==String.class){
                //field.setBoolean(c, Settings.getBoolean(acclSetting.name(), false));
            }
            if(field.getType()==Integer.class) {
                field.setAccessible(true);
                field.setInt(obj, Settings.getInt(field.getName(), -1));
            }
            if(field.getType()==Integer.class) {
                //do nothing, options are static not updatable
            }
            //none of this, treat as a string:
            //field.set(c, Settings.getString(acclSetting.name(), ""));
        }catch (Exception e){
            AcclBus.post("ERROR - "+"updateAnnotation Exception:\n"+e.getMessage());
            return false;
        }
        return true;
    }

}
