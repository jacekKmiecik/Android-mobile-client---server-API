package client.android.gui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import client.android.logic.OrderStatusEnum;
import client.android.webservice.JsonParser;
import client.android.webservice.WebServiceUtils;

public class OrderTab extends Activity {

	private Button changeOrderStatusButton;
	private TextView currentOrderNumberTextView;
	private EditText startAddressEditText;
	private EditText endAddressEditText;
	private EditText orderStatusEditText;

	private Context context;

	private static final int STATUS_IN_PROGRESS = 0;
	private static final int STATUS_REJECTED = 1;
	private static final int STATUS_COMPLETED = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordertab);
		context = this;
		Log.i("other", "Order tab onCreate() was called.");

		changeOrderStatusButton = (Button) findViewById(R.id.changeOrderStatusButton);
		changeOrderStatusButton.setEnabled(false);
		currentOrderNumberTextView = (TextView) findViewById(R.id.currentOrderNumberTextView);
		startAddressEditText = (EditText) findViewById(R.id.startAddressEditText);
		endAddressEditText = (EditText) findViewById(R.id.endAddressEditText);
		orderStatusEditText = (EditText) findViewById(R.id.orderStatusEditText);

		changeOrderStatusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					//this is an implementation of the popup dialog for changing the current order status
					new AlertDialog.Builder(context)
							.setTitle(R.string.changeOrderStatusDialogTitle)
							.setItems(R.array.changeOrderStatusDialogValues,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int dialogIndex) {
											switch (dialogIndex) {
											case STATUS_IN_PROGRESS:
												if(orderStatusEditText.getText().toString().equals(
														OrderStatusEnum.IN_PROGRESS.getValueForGui()) == false){
													Log.i("UpdateOrderStatus",
															"Order status changed to: IN_PROGRESS");
													new UpdateOrderStatus().execute(
															Integer.parseInt((String) currentOrderNumberTextView
																	.getText()),
															OrderStatusEnum.IN_PROGRESS);
													orderStatusEditText.setText(OrderStatusEnum.IN_PROGRESS.getValueForGui());
													AndroidClient.showToastMessage(
															context,
															"Order accepted.");
												}
												else{
													AndroidClient.showToastMessage(
															context,
															"This is the current order status! Select a different one.");	
												}
												break;
											case STATUS_REJECTED:
												changeOrderStatusButton.setEnabled(false);
												Log.i("UpdateOrderStatus",
														"Order status changed to: REJECTED");
												new UpdateOrderStatus().execute(
														Integer.parseInt((String) currentOrderNumberTextView
																.getText()),
														OrderStatusEnum.REJECTED);
												AndroidClient.showToastMessage(
														context,
														"Order rejected.");
												OrderInfo.id_order = 0;
												
												break;
											case STATUS_COMPLETED:
												changeOrderStatusButton.setEnabled(false);
												String s = orderStatusEditText.getText().toString();
												if(s.equals(OrderStatusEnum.NEW.getValueForGui()) == false){
													Log.i("UpdateOrderStatus",
															"Order status changed to: COMPLETED");
													new UpdateOrderStatus().execute(
															Integer.parseInt((String) currentOrderNumberTextView
																	.getText()),
															OrderStatusEnum.COMPLETED);
													AndroidClient.showToastMessage(
															context,
															"Order completed.");
													OrderInfo.id_order = 0;										
												}
												else{
													AndroidClient.showToastMessage(
															context,
															"You need to accept the order first!");
												}
												break;
											}
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									}).show();
				} catch (Exception e) {
					Log.e("other", "dialog", e);
				}

			}
		});

		// initialize listening for upcoming orders:
		new GetOrderSpecs().execute();
	}
	
	//populate the UI with the proper data about the current order:
	public void updateOrderDisplay(int id_order, String address_start,
			String address_end, OrderStatusEnum order_status) {
		// Current order ID:
		if (id_order > 0)
			currentOrderNumberTextView.setText(Integer.toString(id_order));
		else
			currentOrderNumberTextView.setText("No active order");
		// Order's starting address:
		if (address_start != null)
			startAddressEditText.setText(address_start);
		else
			startAddressEditText.setText(R.string.unavailable);
		// Order's destination address:
		if (address_end != null)
			endAddressEditText.setText(address_end);
		else
			endAddressEditText.setText(R.string.unavailable);
		// Current order status:
		if (order_status != null)
			orderStatusEditText.setText(order_status.getValueForGui());
		else
			orderStatusEditText.setText(R.string.unavailable);
	}

	class GetOrderSpecs extends AsyncTask<Integer, Integer, Boolean>
			implements JsonParser {

		private OrderInfo order;
		private final String URL = "http://" + PrefsTab.serverIp
				+ "/logistics/getOrderSpecs.php";

		@Override
		protected Boolean doInBackground(Integer... params) {
			Log.i("order", "Starting GetOrderSpecsAsyncTask thread...");

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.e("order", e.getMessage());
			}
			Log.i("order", "Waking up...");

			int id_vehicle = OrderInfo.id_vehicle;
			if (id_vehicle > 0 && OrderInfo.id_order == 0) {
				String response = WebServiceUtils.invokeWebservice(
						URL,
						new BasicNameValuePair("id_vehicle", Integer
								.toString(id_vehicle)));
				return parseJsonResponse(response);
			} else
				return false;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Boolean orderUpdated) {
			// enable the button
			if (orderUpdated) {
				updateOrderDisplay(OrderInfo.id_order,
						order.getAddress_start(), order.getAddress_end(),
						order.getOrder_status());
				changeOrderStatusButton.setEnabled(true);
				Log.w("order", "Pobrano zlecenie o id=" + OrderInfo.id_order);
				AndroidClient.showToastMessage(getApplicationContext(),
						"Order details updated");
			} else {
				if (OrderInfo.id_order == 0) {
					Log.w("order", "No order currently assigned");
					updateOrderDisplay(0, null, null, null);
					changeOrderStatusButton.setEnabled(false);
				} else
					Log.w("order", "Info about order with id=" + OrderInfo.id_order
							+ " has been fetched from the server.");
			}
			new GetOrderSpecs().execute();
		}

		@Override
		public boolean parseJsonResponse(String response) {

			// parse json data
			try {
				JSONArray jArray = new JSONArray(response);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					int id_vehicle = json_data.getInt("id_vehicle");
					int id_order = json_data.getInt("id_order");
					String address_start = json_data.getString("address_start");
					String address_end = json_data.getString("address_end");
					String order_status = json_data.getString("status");

					order = new OrderInfo(id_order, id_vehicle, address_start,
							address_end, order_status);

					Log.i("JSON", "id_vehicle: " + id_vehicle + ", id_order: "
							+ id_order + ", address_start: " + address_start
							+ ", address_end: " + address_end + ", status: "
							+ order_status);
					OrderInfo.id_order = id_order;

				}
			} catch (JSONException e) {
				Log.e("JSON", "Error parsing data " + e.toString());
				return false;
			}

			return true;
		}

	}

	class UpdateOrderStatus extends AsyncTask<Object, Integer, String> {

		@Override
		protected String doInBackground(Object... params) {
			/*
			 * params[0] - id_order (int) params[1] - nowy status
			 * (OrderStatusEnum)
			 */
			int id_order = (Integer) params[0];
			OrderStatusEnum status = (OrderStatusEnum) params[1];

			Log.i("UpdateOrderStatus", "Changing order status to=" + id_order
					+ "na " + status.getValue());
			updateOrderStatusInDatabase(id_order, status.getValue());

			return status.getValue();
		}

		@Override
		protected void onPostExecute(String status) {
			// enable the button
			if(status.equals(OrderStatusEnum.IN_PROGRESS.getValue()))
					changeOrderStatusButton.setEnabled(true);
		}

		@Override
		protected void onPreExecute() {
			// disable the button until the thread execution is finished:
			changeOrderStatusButton.setEnabled(false);
		}

		private void updateOrderStatusInDatabase(int id_order, String status) {
			Log.i("JSON", "aktualizacja danych o zamówieniu");
			
			WebServiceUtils.invokeWebservice("http://" + PrefsTab.serverIp
				+ "/logistics/updateOrderStatus.php",
				new BasicNameValuePair("id_order", Integer.toString(id_order)),
				new BasicNameValuePair("status", status));									
		}

	}
}
