package pt.lsts.accl.googlemap;


import pt.lsts.accl.android.AcclActivity;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.util.AndroidUtil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by jloureiro on 17-09-2015.
 */
public class MainActivity extends AcclActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();
    }

    public void setupButtons(){
        setupShowPosButton();
        setupGoToMapButton();
    }

    public void setupShowPosButton() {
        Button button = (Button) findViewById(R.id.showPosButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AcclBus.getPosition() != null) {
                    showToastLong(AcclBus.getPosition().toString());
                } else {
                    showToastLong("AcclBus.getPosition()==null");
                }
            }
        });
    }

    public void setupGoToMapButton() {
        Button button = (Button) findViewById(R.id.goToMapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.changeActivity(getApplicationContext(), MapsActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
