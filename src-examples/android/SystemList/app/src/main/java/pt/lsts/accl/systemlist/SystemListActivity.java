package pt.lsts.accl.systemlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import pt.lsts.accl.androidlib.AcclActivity;


public class SystemListActivity extends AcclActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_list);
        setFragmentHolderID(R.id.fragment_placeholder);
        SystemListFragment systemListFragment = new SystemListFragment();

        boolean bool = loadFragment(systemListFragment);
        if (bool==true)
            Log.i(TAG,"systemListFragment loaded");
        if (bool==false)
            Log.e(TAG, "systemListFragment failed to load");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_list, menu);
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

    @Override
    public void onResume(){
        showToastShort(TAG+": onResume");
        super.onResume();

    }


}
