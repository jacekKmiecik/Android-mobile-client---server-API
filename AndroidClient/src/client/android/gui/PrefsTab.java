package client.android.gui;

import client.android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefsTab extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	public static String serverIp;
	public static Boolean autoLocationUpdateOn; 
	public static String geolocationMode;
	
	private final String LOCATION_UPDATES_KEY = "locationUpdates";
	private final String GEOLOCATION_MODES_KEY = "geolocationModes";
	private final String SERVER_IP_KEY = "serverIp";
	private final String DEFAULT_SERVER_IP = "192.168.1.101";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        serverIp = DEFAULT_SERVER_IP;
        settings.edit().putString(SERVER_IP_KEY, DEFAULT_SERVER_IP).commit();
        autoLocationUpdateOn = settings.getBoolean(LOCATION_UPDATES_KEY, true);
        geolocationMode = settings.getString(GEOLOCATION_MODES_KEY, "NETWORK");       
        
        settings.registerOnSharedPreferenceChangeListener(this);        
    }
   

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if(key.equals(LOCATION_UPDATES_KEY)){
			Log.i("prefs", LOCATION_UPDATES_KEY + "changed to " + 
					sharedPreferences.getBoolean(LOCATION_UPDATES_KEY, true));
		}
		else if(key.equals(GEOLOCATION_MODES_KEY)){
			Log.i("prefs", GEOLOCATION_MODES_KEY + "changed to " + 
					sharedPreferences.getString(GEOLOCATION_MODES_KEY, "NETWORK"));	
			
		}
		else if(key.equals(SERVER_IP_KEY)){
			String newValue = sharedPreferences.getString(SERVER_IP_KEY, DEFAULT_SERVER_IP);
			Log.i("prefs", SERVER_IP_KEY + "changed to " + 
					newValue);
			serverIp = newValue;
			
		}
	}
	
}
