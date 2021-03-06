package pt.lsts.accl.settings;


import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;


/**
 * The Factory that builds the Settings Activity.
 * Used by {@link pt.lsts.accl.android.AcclSettingsActivity}.
 *
 * Created by jloureiro on 01-09-2015.
 */
public class SettingsFactory {

    public static final String TAG =  SettingsFactory.class.getName();

	/**
	 * Populate a category with its related settings.
	 *
	 * @param category The Category to be populated.
	 * @param context The Settings Activity Context.
	 */
	public static void populate(PreferenceCategory category, Context context) {
		createHideEntry(category, context);
		Map<String, ?> keys = Settings.getAll();
        SortedSet<String> keysSorted = new TreeSet<String>(keys.keySet());
		for (String key : keysSorted) {
			if (!Settings.getCategory(key, "ERROR").equals(
					category.getTitle()))
				continue;// not in this category
            if (Settings.isOptions(key))
                continue;//do not display options
            if (Settings.hasOptions(key)) {
                createEntry(category, key, Settings.getString(key, "null"), Settings.getOptions(key), context);
                continue;
            }
			if (Settings.getType(key,"ERROR").equalsIgnoreCase(String.class.getName())) {
                createEntry(category, key, Settings.getString(key,"null"), context);
                continue;
            }
            if (Settings.getType(key,"ERROR").equalsIgnoreCase(Integer.class.getName())){
				int valInt = Settings.getInt(key,-1);
				createEntry(category, key,
						valInt, context).getEditText()
						.setInputType(InputType.TYPE_CLASS_NUMBER);
                continue;
			}
            if (Settings.getType(key,"ERROR").equalsIgnoreCase(Boolean.class.getName())){
                boolean valBoolean = Settings.getBoolean(key,false);
                createEntry(category, key,
						valBoolean, context);
                continue;
            }
		}
	}

	/**
	 * Create the Hide Entry to provide an organization way to hide categories.
	 *
	 * @param category The Category to be hidden by the created option.
	 * @param context The Settings Activity Context.
	 */
	public static void createHideEntry(final PreferenceCategory category,
			final Context context) {
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle("Hide Category");
		checkBoxPref.setChecked(false);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
													  Object newValue) {
						hideCategory(category, context);
						return true;
					}
				});
		category.addPreference(checkBoxPref);
	}

	/**
	 * Hide the settings of a category.
	 *
	 * @param category The Category to be hidden.
	 * @param context The Settings Activity Context.
	 */
	public static void hideCategory(final PreferenceCategory category,
			final Context context) {
		category.removeAll();
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle("Hide Category");
		checkBoxPref.setChecked(true);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
													  Object newValue) {
						category.removeAll();
						populate(category, context);

						return true;
					}
				});
		category.addPreference(checkBoxPref);
	}

	/**
	 * Create an entry for a String setting.
	 *
	 * @param category The category the setting belongs to.
	 * @param key The Setting key.
	 * @param valString The Default value to start the setting and display in Settings' Activity.
	 * @param context The Settings Activity Context.
	 * @return The Object representing this Setting editor.
	 */
	public static EditTextPreference createEntry(PreferenceCategory category,
			String key, String valString, Context context) {
		EditTextPreference editTextPreference = new EditTextPreference(context);
		editTextPreference.setTitle(Settings.getKey(key, "ERROR") + ": " + valString);
		editTextPreference.setSummary(Settings.getDescription(key, "null"));
		editTextPreference.setDefaultValue(valString);
		setOnChangeListener(editTextPreference, key);
		category.addPreference(editTextPreference);
		return editTextPreference;
	}

	/**
	 * Create an entry for a Integer setting.
	 *
	 * @param category The category the setting belongs to.
	 * @param key The Setting key.
	 * @param valInteger The Default value to start the setting and display in Settings' Activity.
	 * @param context The Settings Activity Context.
	 * @return The Object representing this Setting editor.
	 */
	public static EditTextPreference createEntry(PreferenceCategory category,
			String key, Integer valInteger, Context context) {

        EditTextPreference editTextPreference = new EditTextPreference(context);
        editTextPreference.setTitle(Settings.getKey(key, "ERROR") + ": " + valInteger);
        editTextPreference.setSummary(Settings.getDescription(key, "null"));
        editTextPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER);
        editTextPreference.setDefaultValue("" + valInteger);
        setOnChangeListener(editTextPreference, key);
        category.addPreference(editTextPreference);
        return editTextPreference;
	}

	/**
	 * Create an entry for a String setting with a set of options.
	 *
	 * @param category The category the setting belongs to.
	 * @param key The Setting key.
	 * @param list The List of possible values to be displayed and choosen from onClick.
	 * @param context The Settings Activity Context.
	 * @return The Object representing this Setting editor.
	 */
    public static ListPreference createEntry(PreferenceCategory category, String key, String defValue, String[] list, Context context){
        ListPreference listPreference = new ListPreference(context);
        listPreference.setTitle(Settings.getKey(key,"ERROR")+": "+defValue);
        listPreference.setSummary(Settings.getDescription(key, "null"));
        listPreference.setDefaultValue(defValue);
        listPreference.setEntries(list);
        listPreference.setEntryValues(list);
        setOnChangeListenerList(listPreference, key);
        category.addPreference(listPreference);
        return listPreference;
    }

	/**
	 * Listenner for changes on Set of String option setting.
	 *
	 * @param listPreference The ListPreference to register listener in.
	 * @param key The key of the setting to be changed.
	 */
    public static void setOnChangeListenerList(final ListPreference listPreference, final String key){
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean result = changeValue(key, newValue);
				preference.setTitle(key + ": " + newValue);
				preference.setDefaultValue(newValue);
				preference.setSummary(Settings.getDescription(key, "null"));

				return result;
			}
		});
    }

	/**
	 * Listenner for Settings' changes.
	 *
	 * @param editTextPreference The Preference to register the listener on.
	 * @param key The key of the setting associated with this preference.
	 */
	public static void setOnChangeListener(
			final EditTextPreference editTextPreference, final String key) {
		editTextPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
													  Object newValue) {
						boolean result = changeValue(key, newValue);
						preference.setTitle(key + ": " + newValue);
						preference.setDefaultValue(newValue);
						preference.setSummary(Settings.getDescription(key, "null"));

						return result;
					}
				});
	}

	/**
	 * Create a boolean entry with a checkbox.
	 *
	 * @param category The category the setting belongs to.
	 * @param key The Setting key.
	 * @param val The Default value to start the setting and display in Settings' Activity.
	 * @param context The Settings Activity Context.
	 * @return The Object representing this Setting editor.
	 */
	public static void createEntry(PreferenceCategory category,
			final String key, Boolean val, Context context) {
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle(Settings.getKey(key, "ERROR"));
        checkBoxPref.setSummary(Settings.getDescription(key,"null"));
		checkBoxPref.setChecked(val);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						boolean result = changeValue(key, newValue);
						preference.setDefaultValue(newValue);
                        preference.setSummary(Settings.getDescription(key,"null"));

						return result;
					}
				});
		category.addPreference(checkBoxPref);
	}

	/**
	 * Change a value of a setting.
	 *
	 * @param key The Setting key.
	 * @param newValue The new value of the setting.
	 * @return false if errors occur, true otherwise.
	 */
	public static boolean changeValue(String key, Object newValue) {
		String type = Settings.getType(key,"ERROR");

		if (type.equalsIgnoreCase(String.class.getName())) {
			return Settings.putString(key, (String) newValue);
		}
		if (type.equalsIgnoreCase(Integer.class.getName())) {
			return Settings.putInt(key, Integer.parseInt((String) newValue));
		}
		if (type.equalsIgnoreCase(Boolean.class.getName())) {
			return Settings.putBoolean(key, (Boolean) newValue);
		}
		return false;
	}

	/**
	 * Create a category of settings.
	 *
	 * @param name The name of the category.
	 * @param context The Settings Activity Context.
	 * @return The obj representing the category to add editors to.
	 */
	public static PreferenceCategory createCategory(String name,
			final Context context) {
		final PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(name);
		category.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (category.getPreferenceCount() > 0)
					category.removeAll();
				else
					populate(category, context);
				return false;
			}
		});
		return category;
	}

	/**
	 * Fetch the categories from the Settings' instance.
	 *
	 * @param context The Settings Activity Context.
	 * @return A vector with the Categories to have the editors add to.
	 */
	public static Vector<PreferenceCategory> fetchCategories(Context context) {
		Vector<PreferenceCategory> preferenceCategories = new Vector<PreferenceCategory>();
		Vector<String> categoriesStrings = new Vector<String>();
		Map<String, ?> keys = Settings.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String categoryString = Settings.getCategory(entry.getKey(),
					"ERROR");
			if (!categoriesStrings.contains(categoryString))
				categoriesStrings.add(categoryString);
		}

		for (String entry : categoriesStrings)
			preferenceCategories.add(createCategory(entry, context));
		preferenceCategories.add(createCategory("Profiles", context));

		return preferenceCategories;
	}

	/**
	 * Fetch the Preferences Buttons:
	 * Save, Load and RESTORE DEFAULTS.
	 *
	 * @param context The Settings Activity Context.
	 * @param activity The Settings Activity Class.
	 * @return
	 */
	public static Vector<Preference> fetchPreferencesButtons(
			final Context context, Class activity) {
		Vector<Preference> preferenceButtons = new Vector<Preference>();
		preferenceButtons.add(createSaveButton(context));
		preferenceButtons.add(createLoadButton(context, activity));
		preferenceButtons.add(createRestoreButton(context, activity));
		return preferenceButtons;

	}

	/**
	 * Create the Save Button.
	 *
	 * @param context The Settings Activity Context.
	 * @return The Save Button Preference obj
	 */
	public static Preference createSaveButton(final Context context) {
		Preference preference = new Preference(context);
		preference.setTitle("Save Profile");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        save(context);
                        return false;
                    }
                });
		return preference;
	}

	/**
	 * Create the Load Button.
	 *
	 * @param context The Settings Activity Context.
	 * @return The Load Button Preference obj
	 */
	public static Preference createLoadButton(final Context context, final Class activity) {
		Preference preference = new Preference(context);
		preference.setTitle("Load Profile");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						load(context, activity);
						return false;
					}
				});
		return preference;
	}

	/**
	 * Create the RESTORE DEFAULTS Button.
	 *
	 * @param context The Settings Activity Context.
	 * @return The RESTORE DEFAULTS Button Preference obj
	 */
	public static Preference createRestoreButton(final Context context, final Class actitivity) {
		Preference preference = new Preference(context);
		preference.setTitle("RESTORE DEFAULTS");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						restoreDefaults(context, actitivity);
						return false;
					}
				});
		return preference;
	}

	/**
	 * Restore the default settings. Load the settings from default_settings.csv file.
	 *
	 * @param context The Settings Activity Context.
	 * @param activity The Settings Activity Class.
	 */
	public static void restoreDefaults(final Context context, final Class activity) {
		new AlertDialog.Builder(context)
				.setTitle("Restore Settings Default")
				.setMessage(
						"Are you sure you want to restore settings default?\nWARNING: this action is permenant")
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String result = Profile.restoreDefaults();
								Toast.makeText(context, result,
										Toast.LENGTH_SHORT).show();
								regenerateActivity(context, activity);
							}
						})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						}).create().show();
	}

	/**
	 * Load the settings from a file chosen from a list.
	 *
	 * @param context The Settings Activity Context.
	 * @param activity The Settings Activity Class.
	 */
	public static void load(final Context context, final Class activity) {
		final String[] array = Profile.getProfilesAvailable();
		new AlertDialog.Builder(context).setTitle("Choose a Profile:")
				.setItems(array, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String result = Profile.load(array[which]);
						Toast.makeText(context, result, Toast.LENGTH_SHORT)
								.show();
						regenerateActivity(context, activity);
					}
				}).create().show();
	}

	/**
	 * Save the current Settings in a file. Name of file to be chosen from a prompt.
	 *
	 * @param context The Settings Activity Context.
	 */
	public static void save(final Context context) {
		final EditText input = new EditText(context);
		new AlertDialog.Builder(context)
				.setTitle("Save Profile")
				.setMessage("Select a name for Profile:")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String result = Profile.save(value.toString());
                        Toast.makeText(context, result, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
				.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // canceled
                            }
                        }).create().show();
	}

	/**
	 * Regenerate the Activity uppon loading/saving/restoring defaults.
	 *
	 * @param context The Settings Activity Context.
	 * @param activity The Settings Activity Class.
	 */
	public static void regenerateActivity(Context context, Class activity) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);
	}


}
