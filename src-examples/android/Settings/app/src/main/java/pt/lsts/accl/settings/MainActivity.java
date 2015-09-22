package pt.lsts.accl.settings;


import pt.lsts.accl.androidlib.AcclActivity;
import pt.lsts.accl.util.AndroidUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.squareup.otto.Subscribe;


/**
 *
 * Simple MainActivity with a Button to access an extension of the {@link pt.lsts.accl.androidlib.AcclSettingsActivity}.
 *
 *
 * Created by jloureiro on 03-09-2015.
 */
public class MainActivity extends AcclActivity {

    /**
     * Set the button listenner.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonListenner();
    }

    /**
     * Change the Activity to the Settings one.
     */
    public void setButtonListenner(){
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.changeActivity(getBaseContext(), SettingsActivity.class);
            }
        });
    }

    /**
     * Auto-generated.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Auto-generated.
     * @param item
     * @return
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

}
