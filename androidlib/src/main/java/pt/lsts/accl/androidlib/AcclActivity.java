package pt.lsts.accl.androidlib;


import pt.lsts.accl.bus.AcclBus;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 *
 * AcclActivity to be extended by user.
 *
 * Includes a fragmentHolder and methods to handle its fragments.
 * Keep track of registered Listenners and unregister them onPause.
 * Methods to aid register/unregister listenners
 * Useful showToast methods.
 *
 * Created by jloureiro on 11-08-2015.
 *
 */
public class AcclActivity extends AppCompatActivity{

    public static String TAG = "TAG";
    private HashSet<Integer> registeredListeners;
    private int fragmentHolderID;
    private boolean fragmentHolderIDsetBool=false;

    /**
     *
     * Android's standard onCreateOptionsMenu override.
     * Please refer to {@link AppCompatActivity#onCreate(Bundle)}
     *
     * @param savedInstanceState
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        registeredListeners = new HashSet<Integer>();
    }

    /**
     *
     * Android's standard onCreateOptionsMenu override.
     * Please refer to {@link AppCompatActivity#onCreateOptionsMenu(Menu)}
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accl, menu);
        return true;
    }

    /**
     *
     * Android's standard onOptionsItemSelected override.
     * Please refer to {@link AppCompatActivity#onOptionsItemSelected(MenuItem)}
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
     *
     * Android's standard onPause override
     * Please refer to {@link AppCompatActivity#onPause()}
     *
     */
    @Override
    public void onPause(){
        super.onPause();
        unregisterAll();
        AcclBus.onPause();
    }

    /**
     *
     * Android's standard onResume override.
     * Please refer to {@link AppCompatActivity#onResume()}
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        AcclBus.onResume();
    }

    public boolean loadFragments(ArrayList<AcclFragment> fragmentList) {
        boolean bool=true;
        for (AcclFragment fragment : fragmentList){
            if (loadFragment(fragment)==false)
                bool=false;
        }
        return bool;
    }

    public boolean loadFragment(AcclFragment fragmentObj){
        if (fragmentHolderIDsetBool==false){
            AcclBus.post("ERROR - "+"FragmentHolderID Not Set");
            return false;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(getFragmentHolderID(), fragmentObj);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
        return true;
    }

    public void removeFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void removeFragments(ArrayList<Fragment> fragmentList){
        for (Fragment fragment : fragmentList)
            removeFragment(fragment);
    }

    public void removeAllFragments(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            removeFragment(fragment);
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

    /**
     * Get the FragmentHolderID to search for subViews.
     *
     * @return The ID from R file of the Fragment Holder of this Activity.
     *
     */
    public int getFragmentHolderID() {
        return fragmentHolderID;
    }

    /**
     * Set the FragmentHolderID to search for subViews.
     *
     * @param fragmentHolderID The new ID from R file for this Activity's Fragment Holder.
     *
     */
    public void setFragmentHolderID(int fragmentHolderID) {
        this.fragmentHolderID = fragmentHolderID;
        fragmentHolderIDsetBool=true;
    }


}
