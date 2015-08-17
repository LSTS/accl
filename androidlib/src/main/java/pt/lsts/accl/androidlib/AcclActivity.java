package pt.lsts.accl.androidlib;


import pt.lsts.accl.bus.AcclBus;

import java.util.HashSet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


/**
 *
 */
public class AcclActivity extends AppCompatActivity{

    private HashSet<Integer> registeredListeners;
    public static String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        setContentView(R.layout.activity_accl);
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
        for (Object pojo : registeredListeners){
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

}
