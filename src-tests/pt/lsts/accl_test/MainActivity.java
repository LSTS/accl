package pt.lsts.accl_test;

import com.squareup.otto.Subscribe;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.accl.event.EventSystemConnected;
import pt.lsts.imc.Announce;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	static TextView hello;
	private EventMainSystemSelected selectedSystem = null;
	
	@Subscribe
	public void on(Announce ann) {
		Log.i("ANNOUNCE", ann.getSourceName());
	}
	
	@Subscribe 
	public void on(EventSystemConnected event) {
		
		System.out.println(event.getSysName()+" is now connected");
		
		if (selectedSystem == null) {
			System.out.println("selecting "+event.getSysName()+" as main system");
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
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

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
	protected void onDestroy() {
		AcclBus.forget(this);
		super.onDestroy();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			//System.out.println("AcclBus: "+AcclBus.class.toString());
			hello = ((TextView)rootView.findViewWithTag("hello"));
			Log.i(hello.toString(),"Created TextBox.");
			hello.setText("Hello AcclBus.");
			
			return rootView;
		}
	}
}
