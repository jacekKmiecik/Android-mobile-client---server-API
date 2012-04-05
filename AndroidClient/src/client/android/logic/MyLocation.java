package client.android.logic;


import client.android.AndroidClient;
import client.android.gui.LocationTab;
import client.android.gui.PrefsTab;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation {
	public static LocationManager lm;
	private LocationResult locationResult;
	private boolean gpsEnabled;
	private boolean networkEnabled;
	private static final int minute = 60000;

	public boolean getLocation() {
		gpsEnabled = false;
		networkEnabled = false;
		
		Context context = AndroidClient.getContext();
		locationResult = LocationTab.locationResult;

		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		Log.i("prefs", "geolocationMode="+PrefsTab.geolocationMode);
		if(PrefsTab.geolocationMode.equals("NETWORK") == false){ //just the network, gpsEnabled = false
			try {
				gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
				Log.i("geo", "Geolocation based on GPS is ON.");
			} catch (Exception ex) {
				Log.i("geo", "Geolocation based on GPS is OFF.");
			}
		}

		if(PrefsTab.geolocationMode.equals("GPS") == false){ //just the GPS witout network, networkEnabled = false
			try {
				networkEnabled = lm
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				Log.i("geo", "Geolocation based on network is ON.");
			} catch (Exception ex) {
				Log.i("geo", "Geolocation based on network is ON.");
			}
		}

		// don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled) {
			Log.i("geo", "No geolocation providers are enabled.");
			return false;
		}

		if (gpsEnabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);			
			Log.i("geo", "Listening for GPS location updates.");
		}
		if (networkEnabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minute, 0,
					locationListenerNetwork);
			Log.i("geo", "Listening for network location updates.");
		}

		return true;
		
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			locationResult.gotLocation(location);
			Log.i("geo", "Obtained new location from GPS.");
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			locationResult.gotLocation(location);
			Log.i("geo", "Obtained new location from network.");			
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};	

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
