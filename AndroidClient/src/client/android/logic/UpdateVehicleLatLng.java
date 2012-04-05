package client.android.logic;

import org.apache.http.message.BasicNameValuePair;


import client.android.gui.PrefsTab;
import client.android.webservice.WebServiceUtils;
import android.os.AsyncTask;

//params, progress, result
public class UpdateVehicleLatLng extends
		AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		// params[0] - latitude
		// params[1] - longitude
		// params[2] - id_vehicle

		WebServiceUtils.invokeWebservice("http://" + PrefsTab.serverIp
				+ "/logistics/updateVehicleLatLng.php", 
				new BasicNameValuePair("lat", params[0]),
				new BasicNameValuePair("lng", params[1]),
				new BasicNameValuePair("id_vehicle", params[2]));

		return null;
	}
}
