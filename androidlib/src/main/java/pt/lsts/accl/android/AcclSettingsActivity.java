package pt.lsts.accl.android;


import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.settings.Settings;
import pt.lsts.accl.settings.SettingsFactory;

import java.util.HashSet;
import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;


/**
 *
 * Provides a User Interface to see and modify settings and profiles.
 *
 *
 * Created by jloureiro on 01-09-2015.
 */
public class AcclSettingsActivity extends PreferenceActivity {

    public static String TAG = "TAG";
    private HashSet<Integer> registeredListeners;
    private int fragmentHolderID;
    private boolean fragmentHolderIDsetBool=false;
	PreferenceScreen preferenceScreen;

    /**
     * Similar to {@link AcclActivity}, instantiate a registeredListeners of {@link AcclBus}.
     *
     * @param savedInstanceState the saved instance to be restored
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        registeredListeners = new HashSet<Integer>();
	}

    /**
     * Creates the screen, everything is generated dynamically on this method
     * Fetches the categories and their respective settings.
     *
     */
    @Override
    protected void onResume(){
        super.onResume();
        AcclBus.onResume();
        Settings.registerACCLSettingsAnnotationsFromClass(this.getClass());

        preferenceScreen = getPreferenceManager().createPreferenceScreen(this);

        Vector<PreferenceCategory> preferenceCategories = SettingsFactory
                .fetchCategories(this);

        for (PreferenceCategory preferenceCategory : preferenceCategories)
            preferenceScreen.addPreference(preferenceCategory);

        populateCategories(preferenceCategories);

        setPreferenceScreen(preferenceScreen);


    }

    /**
     * Populates ALL categories with their respective settings.
     *
     * @param preferenceCategories The Vector with the Categories.
     */
	public void populateCategories(
			Vector<PreferenceCategory> preferenceCategories) {
		for (PreferenceCategory preferenceCategory : preferenceCategories) {
			if (preferenceCategory.getTitle().equals("Profiles")) {
				createProfileCategory(preferenceCategory);
				continue;
			}
			SettingsFactory.populate(preferenceCategory, this);
		}
	}

    /**
     * Creates the profile category with the buttons:
     * Save, Load, RESTORE DEFAULTS.
     *
     * @param preferenceCategory The category to create the buttons on.
     */
	public void createProfileCategory(PreferenceCategory preferenceCategory) {
		Vector<Preference> preferenceButtons = SettingsFactory
				.fetchPreferencesButtons(this, this.getClass());

		for (Preference preferenceButton : preferenceButtons)
			preferenceCategory.addPreference(preferenceButton);
	}

    /**
     * Remove the Categories and settings, unregister listenners and Pause AcclBus.
     *
     */
    @Override
    protected void onPause(){
        Log.i(TAG, "AcclSettingsActivity.onPause() called");
        super.onPause();
        preferenceScreen.removeAll();
        preferenceScreen=null;
        finish();
        unregisterAll();
        AcclBus.onPause();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /**
     *
     * Register a listenner with AcclBus using AcclBus register method.
     * Please refer to {@link pt.lsts.accl.bus.AcclBus#register(Object)}
     *
     * @param pojo The Listenner to be registed.
     *
     */
    public void register(Object pojo) {
        AcclBus.register(pojo);
        registeredListeners.add(pojo.hashCode());
    }

    /**
     *
     * Unregister a listenner with AcclBus using AcclBus unregister method.
     * Please refer to {@link pt.lsts.accl.bus.AcclBus#unregister(Object)}
     *
     * @param pojo The Listenner to be unregisted.
     *
     */
    public synchronized void unregister(Object pojo) {
        AcclBus.unregister(pojo);
        registeredListeners.remove(pojo.hashCode());
    }

    /**
     *
     * Unregister all listenners with AcclBus.
     *
     */
    public void unregisterAll(){
        for (Object pojo : registeredListeners) {
            unregister(pojo);
        }
    }

    /**
     * Show a long message for a short time via a Toast.
     * Please refer to {@link android.widget.Toast}
     *
     * @param msg The Message to be shown.
     *
     */
    public void showToastShort(final String msg){
        final Context context = this.getApplicationContext();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Show a long message for a long time via a Toast.
     * Please refer to {@link android.widget.Toast}
     *
     * @param msg The Message to be shown.
     *
     */
    public void showToastLong(final String msg){
        final Context context = this.getApplicationContext();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

}
