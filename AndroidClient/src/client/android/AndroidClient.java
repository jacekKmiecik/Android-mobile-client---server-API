package client.android;

import client.android.gui.LocationTab;
import client.android.gui.LoginTab;
import client.android.gui.OrderTab;
import client.android.gui.PrefsTab;
import client.android.logic.OrderInfo;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class AndroidClient extends TabActivity {

	private TabHost tabHost;
	private static AndroidClient instance;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		OrderInfo.id_vehicle = 0;
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		Resources res = getResources(); // Resource object to get Drawables
	
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, OrderTab.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("order").setIndicator("Order",
                res.getDrawable(R.drawable.ic_tab_order))
				.setContent(intent);
		tabHost.addTab(spec);		
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, LocationTab.class);
		spec = tabHost.newTabSpec("location").setIndicator("Location",
                res.getDrawable(R.drawable.ic_tab_location))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PrefsTab.class);
		spec = tabHost.newTabSpec("prefs").setIndicator("Settings",
                res.getDrawable(R.drawable.ic_tab_options))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, LoginTab.class);
		spec = tabHost.newTabSpec("login").setIndicator("Login",
                res.getDrawable(R.drawable.ic_tab_login))
				.setContent(intent);
		tabHost.addTab(spec);

		//hides the on-screen keyboard when the user switches to another tab:
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						tabHost.getApplicationWindowToken(), 0);
				
			}
		});
		
		
		//make the app go through all the tabs, so all the threads associated with a particular tab can 
		//be initialized. This applies to updates that are done in the background (downloading info about 
		//new tasks, location updates etc.)		
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(2);
		//set the login tab as the default one:
		tabHost.setCurrentTab(3);
		
		//initialize listening for location updates (if it's not running already):
		if(PrefsTab.autoLocationUpdateOn) LocationTab.myLocation.getLocation();
		Log.i("prefs", "przy uruchomieniu autoLocationUpdateOn=" + PrefsTab.autoLocationUpdateOn);
	}
	
    public static Context getContext() {
        return instance;
    }
	
	public static void showToastMessage(Context context, String msg){
		try{
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.show();
		}
		catch (Exception e) {
			Log.i("other", "Error displaying toast message: '" + msg + "'");
		}
	}
}