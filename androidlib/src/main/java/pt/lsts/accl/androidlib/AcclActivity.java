package pt.lsts.accl.androidlib;


import pt.lsts.accl.bus.AcclBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 *
 */
public class AcclActivity extends AppCompatActivity{

    public static String TAG = "TAG";
    private HashSet<Integer> registeredListeners;
    private int fragmentHolderID;
    private boolean fragmentHolderIDsetBool=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        //setContentView(R.layout.activity_accl);
        registeredListeners = new HashSet<Integer>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accl, menu);
        return true;
    }

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


    public void register(Object pojo) {
        AcclBus.register(pojo);
        registeredListeners.add(pojo.hashCode());
    }


    public synchronized void unregister(Object pojo) {
        AcclBus.unregister(pojo);
        registeredListeners.remove(pojo.hashCode());
    }


    public void unregisterAll(){
        for (Object pojo : registeredListeners) {
            unregister(pojo);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterAll();
    }

    @Override
    public void onResume(){
        super.onResume();
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(getFragmentHolderID(), fragmentObj);
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
        return true;
    }

    public int getFragmentHolderID() {
        return fragmentHolderID;
    }

    public void setFragmentHolderID(int fragmentHolderID) {
        this.fragmentHolderID = fragmentHolderID;
        fragmentHolderIDsetBool=true;
    }


}
