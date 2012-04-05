package client.android.gui;

import java.util.Date;
import client.android.R;
import client.android.logic.MyLocation;
import client.android.logic.OrderInfo;
import client.android.logic.UpdateVehicleLatLng;
import client.android.logic.MyLocation.LocationResult;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class LocationTab extends Activity {

	private static LocationTab instance;
	private static EditText longitudeEditText;
	private static EditText latitudeEditText;
	private static EditText updatedOnEditText;

	private static Location l = null;
	public static MyLocation myLocation = new MyLocation();

	public static LocationResult locationResult = new LocationResult() {
		@Override
		public void gotLocation(Location location) {

			l = location;
			updateLocationDisplay();

			// update location in the database (if the user is logged in -> id_vehicle != 0)
			if (OrderInfo.id_vehicle > 0)
				new UpdateVehicleLatLng().execute(
						Double.toString(l.getLatitude()),
						Double.toString(l.getLongitude()),
						Integer.toString(OrderInfo.id_vehicle));
			else Log.w("geo", "No vehicle currently assigned");
		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationtab);

		longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
		latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
		updatedOnEditText = (EditText) findViewById(R.id.updatedOnEditText);

	}

	private static void updateLocationDisplay() {
		longitudeEditText.setText(Double.toString(l.getLongitude()));
		latitudeEditText.setText(Double.toString(l.getLatitude()));
		updatedOnEditText.setText(new Date(l.getTime()).toLocaleString());
	}

	public static Context getContext() {
		return instance;
	}
}
