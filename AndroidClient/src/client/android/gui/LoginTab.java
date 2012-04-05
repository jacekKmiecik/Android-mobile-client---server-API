package client.android.gui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import client.android.AndroidClient;
import client.android.R;
import client.android.logic.OrderInfo;
import client.android.logic.PingServer;
import client.android.webservice.JsonParser;
import client.android.webservice.WebServiceUtils;


public class LoginTab extends Activity {

	public static boolean userLoggedOn;

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private TextView idVehicleTextView;
	private TextView licensePlateTextView;
	private TextView brandTextView;
	public static TextView pingTextView;

	private static final boolean LAYOUT_LOGGED_IN = true;
	private static final boolean LAYOUT_NOT_LOGGED_IN = false;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		loginEditText = (EditText) findViewById(R.id.loginEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		loginButton = (Button) findViewById(R.id.loginButton);
		idVehicleTextView = (TextView) findViewById(R.id.idVehicleTextView);
		licensePlateTextView = (TextView) findViewById(R.id.licensePlateTextView);
		brandTextView = (TextView) findViewById(R.id.brandTextView);
		pingTextView = (TextView) findViewById(R.id.pingTextView);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (userLoggedOn == false) { //log IN
					Log.i("login", "userLoggedOn = " + userLoggedOn);
					Log.i("login", "Loguje sie u¿ytkownik "
							+ loginEditText.getText().toString());
					new UserLogin().execute();
				} else { //log OUT
					OrderInfo.id_order = 0;
					OrderInfo.id_vehicle = 0;
					userLoggedOn = false;	
					updateLoginTabLayout(LAYOUT_NOT_LOGGED_IN);
				}
			}
		});		

		// initialize the thread that regularly pings the server:
		new PingServer().execute(PrefsTab.serverIp);

	}

	/*
	 * Method which alters the user interface when he or she logs in or out
	 */
	private void updateLoginTabLayout(boolean newLayout) {
		// user logges in, the username and password fields are greyed out and not editable
		if (newLayout == LAYOUT_LOGGED_IN) {
			loginEditText.setEnabled(false);
			passwordEditText.setEnabled(false);
			loginButton.setText("Log out");
		} else { // user logges out, the username and password fields are cleared and enabled
			loginEditText.setEnabled(true);
			loginEditText.setText("");
			passwordEditText.setEnabled(true);
			passwordEditText.setText("");
			loginButton.setText("Log in");
			idVehicleTextView.setText("");
			licensePlateTextView.setText("");
			brandTextView.setText("");
			AndroidClient.showToastMessage(getContext(),
					"You've been logged out.");
		}
	}

	private Context getContext() {
		return getApplicationContext();
	}

	class UserLogin extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// check if the login and password combination is correct:
			if (checkLoginAndPassword(loginEditText.getText().toString(),
					passwordEditText.getText().toString()) == true) {
				Log.i("login", "Logged in as user "
						+ loginEditText.getText().toString());
				userLoggedOn = true;
				return true;
			} else {
				Log.i("login", "Login failed");
				userLoggedOn = false;
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean loginSuccessful) {
			loginButton.setEnabled(true);
			if (loginSuccessful == true) {
				updateLoginTabLayout(LAYOUT_LOGGED_IN);
				AndroidClient.showToastMessage(getContext(),
						"You've been logged in.");
				new GetUserInfo().execute(OrderInfo.id_vehicle);
			} else {
				AndroidClient.showToastMessage(getContext(),
						"Login failed.");
			}
		}

		@Override
		protected void onPreExecute() {
			loginButton.setEnabled(false);
		}

		private boolean checkLoginAndPassword(String login, String password) {
			Log.i("JSON", "Client authentication: " + login + "/" + password);
			String response = WebServiceUtils.invokeWebservice(
					"http://" + PrefsTab.serverIp + "/logistics/authenticateClient.php", 
					new BasicNameValuePair("login", login),
					new BasicNameValuePair("password", password));

			if(response.equals("failed\n")){
				Log.w("login", "Access denied for user " + login);
				return false;
			}
			else{
				/*
				 * The web service returns a string with a new line sign at the end,
				 * to correctly parse the value, the last 2 characters ("\n") are
				 * trimmed. All what is left is the ID number of the vehicle assigned
				 * to the user which logged in
				 */
				int id_vehicle = Integer.parseInt(
						response.substring(0,response.length()-1));
				Log.i("login", "User " + login + " logged in.");
				Log.i("login", "The logged in user has a vehicle assigned with ID:  " + id_vehicle);
				OrderInfo.id_vehicle = id_vehicle;				
				return true;				
			}
		}
	}

	class GetUserInfo extends AsyncTask<Integer, Void, Boolean> implements JsonParser{

		private String[] driverInfo;
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			int id_vehicle = params[0];		
			Log.i("GetUserInfo", "Trying to GetUserInfo, id_driver=" + id_vehicle);						
			return getUserInfo(id_vehicle);
		}
		
		@Override
		protected void onPostExecute(Boolean sucessfulUpdate) {
			if(sucessfulUpdate){
				idVehicleTextView.setText(driverInfo[0]);
				licensePlateTextView.setText(driverInfo[1]);
				brandTextView.setText(driverInfo[2]);
			}
		}
		
		private Boolean getUserInfo(int id_vehicle){
			driverInfo = new String[3];
			for(int i=0; i<3; i++) driverInfo[i]="";
			
			String response = WebServiceUtils.invokeWebservice(
					"http://" + PrefsTab.serverIp + "/logistics/getDriverInfo.php", 
					new BasicNameValuePair("id_vehicle", Integer.toString(id_vehicle)));
			
			if(parseJsonResponse(response))	return true;			
			else return false;
		}

		@Override
		public boolean parseJsonResponse(String response) {
			
			// parse json data
			try {
				JSONArray jArray = new JSONArray(response);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					String id_vehicle = json_data.getString("id_vehicle");
					String license_plate = json_data.getString("license_plate");
					String brand = json_data.getString("brand");

					Log.i("driverInfo", "id_vehicle: " + id_vehicle + 
							", license_plate: " + license_plate + 
							", brand: " + brand);
					
					driverInfo[0] = id_vehicle;
					driverInfo[1] = license_plate;
					driverInfo[2] = brand;
					OrderInfo.id_vehicle = Integer.parseInt(id_vehicle);
				}
			} catch (JSONException e) {
				Log.e("driverInfo", "Error parsing data " + e.toString());
				return false;
			}	
			
			return true;
		}				
	}	
}
