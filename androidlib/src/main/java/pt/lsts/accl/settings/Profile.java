package pt.lsts.accl.settings;


import pt.lsts.accl.util.FileOperations;
import pt.lsts.accl.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import android.util.Log;


/**
 *
 *
 * Created by jloureiro on 01-09-2015.
 */
public class Profile {

    public static final String TAG = Profile.class.getSimpleName();

	public static String defaultSettingsName = "default_settings";
	public static File defaultSettingsFile = new File(FileOperations.getMainDir(),
			defaultSettingsName);
	public static String firstLineInCsvFile = "type,category,key,description,value(s)";
	public static final String extension = "csv";

	/**
	 * Restore the defaults, load the default_settings.csv file provided in your Assets folder.
	 *
	 * @return a String msg with the result of the load to be displayed in a {@link android.widget.Toast}.
	 */
	public static String restoreDefaults() {
		return load(defaultSettingsName);
	}

	/**
	 * Load a Profile from a .csv file in the device.
	 *
	 * @param name The name of the file to be loaded WITHOUT the extension. usually chosen form a List.
	 * @return a String message with the result of the load to be displayed in a {@link android.widget.Toast}.
	 */
	public static String load(String name) {
		File profile = new File(FileOperations.getMainDir(), name+"."+extension);
		if (!profile.exists())
			return "Profile file:\n" + name + "\nNot Available";
		Vector<String> settings = FileOperations.readLines(profile);
		if (!settings.get(0).equals(firstLineInCsvFile))
			return "Invalid File";
		settings.remove(0);// remove first description line
		Settings.clear();
		for (String setting : settings)
			loadSetting(setting);
		return "Load of file:\n" + name + "\nSucessful!";
	}

	/**
	 * Load a Setting from a line.
	 *
	 * @param setting The string with the line from the .csv format.
	 * @return false if failed to load setting, true otherwise.
	 */
	public static boolean loadSetting(String setting) {
        String parts[] = setting.split(",");
		if (parts.length<=0)
			return false;
		if (parts[0].length()<=0)
			return false;
        if (parts[0].charAt(0) == '#'){
            Log.i(TAG,"setting commented: "+setting);
            return false;
        }
		if (StringUtils.validateSetting(setting)==false){
            Log.i(TAG,"setting not valid: "+setting);
            return false;
        }
        String key = parts[2];
        return Settings.putFullString(key,setting);
		//Log.e(TAG, "Line not added:" + setting);

	}

	/**
	 * Save a set of settings to a .csv file stored in the device accessible and editable.
	 *
	 * @param name the name to give the file WITHOUT the extension
	 * @return String with a msg of sucess or failure of saving the file which can be displayed in {@link android.widget.Toast} or {@link Log}
	 */
	public static String save(String name) {
		Vector<String> lines = new Vector<String>();
		lines.add(firstLineInCsvFile);
		Map<String, ?> keys = Settings.getAll();

		if (keys.size() == 0) {
			Log.e("save", "Settings.getAll().size()==0");
			return "ERROR: settings empty";
		}
		File file = new File(FileOperations.getMainDir(), name + "." + extension);
		FileOperations.initDir(FileOperations.getMainDir());
        SortedSet<String> keysSorted = new TreeSet<String>(keys.keySet());
        for (String key : keysSorted) {
			String type = Settings.getType(key, "java.lang.String");
            String category = Settings.getCategory(key, "category");
            //String key;
            String description = Settings.getDescription(key, " ");
            String val = "";
            if (Settings.isOptions(key)){
                for (String s : Settings.getStrings(key, new String[0])){
                    val += s +",";
                }
                val = val.substring(0, val.length()-1);
            }else {
                switch (type) {
                    case "java.lang.Integer":
                        val += Settings.getInt(key, -1);
                        break;
                    case "java.lang.Boolean":
                        val += Settings.getBoolean(key, false);
                        break;
                    case "java.lang.String":
                        val = Settings.getString(key, "");
                        break;
                }
            }
			String line = type+","+category+","+key+","+description+","+val;
			lines.add(line);
		}
		FileOperations.writeLines(lines, file);
		return "Save of file to:\n" + name + "\nSucessful!";
	}

	/**
	 * Gets the Profile associated .csv files in the device.
	 *
	 * @return An array of Strings with the filenames available in the device WITHOUT the extension.
	 */
	public static String[] getProfilesAvailable() {
		String[] filesArray = FileOperations.getMainDir().list();
		String[] result = FileOperations.filterFilesByExtension(filesArray,extension);
		result = FileOperations.removeExtension(result, extension);

		return result;
	}

}
