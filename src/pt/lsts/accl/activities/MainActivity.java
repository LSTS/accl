package pt.lsts.accl.activities;

import pt.lsts.accl.ACCL;
import pt.lsts.accl.settings.Settings;
import pt.lsts.accl.sys.Sys;
import pt.lsts.accl.R;
import pt.lsts.accl.util.AndroidUtil;

import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.util.Log;
import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.imc.Announce;

import com.squareup.otto.Subscribe;


public class MainActivity extends FragmentActivity {

	public static final String TAG = "MainActivity";
	
	Button chooseActiveSystemButton;
    Button systemListButton;
	//Button manualButton;
	//Button autoButton;
	Button settingsCheckListButton;
	Button testButton;
	Button test2Button;
	
	private EventMainSystemSelected selectedSystem = null;
	
	@Subscribe
	public void on(Announce ann) {
		Log.i("ANNOUNCE", ann.getSourceName());
	}

	@Subscribe
	public void on(EventSystemConnected event) {
		System.out.println(event.getSysName() + " is now connected");
		if (selectedSystem == null) {
			System.out.println("selecting " + event.getSysName()
					+ " as main system");
			selectedSystem = new EventMainSystemSelected(event.getSysName());
			AcclBus.post(selectedSystem);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AcclBus.bind("accldroid", 6009);
		AcclBus.register(this);
		Log.i("CREATE", "Activity created");
		
		setContentView(R.layout.activity_main);

		chooseActiveSystemButton = (Button) findViewById(R.id.chooseActiveSysButton);
		setChooseActiveSystemButton(this);
        systemListButton = (Button) findViewById(R.id.systemListButton);
        setSystemListButton();
		//manualButton = (Button) findViewById(R.id.manualButton);
		//setManualButton();
		//autoButton = (Button) findViewById(R.id.autoButton);
		//setAutoButton();
		settingsCheckListButton = (Button) findViewById(R.id.settingsCheckListButton);
		setSettingsCheckListButton();
		testButton = (Button) findViewById(R.id.testButton);
		testButton.setText("Show All Settings");
		setTestButton();
		test2Button = (Button) findViewById(R.id.test2Button);
		test2Button.setText("Show Active System");
		setTest2Button();

	}

    public void setSystemListButton(){
        systemListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        SystemListActivity.class);
                startActivity(i);
            }
        });
    }

	public void test() {
		// showToast(sharedPreferences.getString("username", "NA"));
		Map<String, ?> keys = Settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String type = Settings.getType(entry.getKey(),"null");
            String cat =Settings.getCategory(entry.getKey(),"null");
            String key = Settings.getKey(entry.getKey(),"null");
            String description = Settings.getDescription(entry.getKey(),"null");
            String val ="null";
            if (type.equalsIgnoreCase("java.lang.String"))
			    val = Settings.getString(key,"null");
            if (type.equalsIgnoreCase("java.lang.Integer"))
                val = String.valueOf(Settings.getInt(key,-1));
            if (type.equalsIgnoreCase("java.lang.Boolean"))
                val = String.valueOf(Settings.getBoolean(key,false));
            AndroidUtil.showToastLong(this, type + "," + cat +"," + key + "," + description + "," + val);
		}
		
	}

	public void test2() {
		if (ACCL.getInstance().getActiveSys() != null)
			AndroidUtil.showToastLong(this, ACCL.getInstance().getActiveSys().getName());
		else
			AndroidUtil.showToastLong(this,"Null");

	}
	
	public void setChooseActiveSystemButton(final Context context){
		chooseActiveSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseActiveSystem(context);
            }
        });
	}
	
	public void chooseActiveSystem(Context context){
		
		final ArrayList<Sys> arrayListSys = ACCL.getInstance().getSystemList().getList();
		final ArrayList<String> arrayListName = ACCL.getInstance().getSystemList().getNameList();
		final String[] array = new String[arrayListName.size()];
		for (int i=0;i<array.length;i++){
			array[i]=arrayListName.get(i);
		}
		
		createChooseActiveSystemDialog(context, array, arrayListSys);
	}
	
	public void createChooseActiveSystemDialog(Context context, String[] array, final ArrayList<Sys> arrayListSys){
		
		new AlertDialog.Builder(context).setTitle("Choose a System:")
		.setItems(array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ACCL.getInstance().setActiveSys(arrayListSys.get(which));
			}
		}).create().show();
		
	}

	public void setTestButton() {
		testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
	}

	public void setTest2Button() {
		test2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2();
            }
        });
	}

	public void setSettingsCheckListButton() {
		settingsCheckListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        SettingsActivity.class);
                startActivity(i);
            }
        });
	}

//	public void setManualButton() {
//		manualButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent i = new Intent(getApplicationContext(),
//                //        ManualActivity.class);
//                //startActivity(i);
//            }
//        });
//	}

//	public void setAutoButton() {
//		autoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent i = new Intent(getApplicationContext(),
//                //        AutoActivity.class);
//                //startActivity(i);
//            }
//        });
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		ACCL.getInstance().setMode(ACCL.MODE.NONE);
        resumeACCL();
	}

	@Override
	protected void onDestroy() {
		AcclBus.forget(this);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setNegativeButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						back();
					}
				}).create().show();

	}

	public void back() {
		super.onBackPressed();
		pauseACCL();
		onStop();
	}

	public void pauseACCL() {
		if (ACCL.getInstance() != null)
			if (ACCL.getInstance().started)
				ACCL.getInstance().pause();
	}

	public void resumeACCL() {
		if (ACCL.getInstance() != null)
			if (!ACCL.getInstance().started)
				ACCL.getInstance().start();
	}

}
