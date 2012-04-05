package client.android.webservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class WebServiceUtils {

	public static String invokeWebservice(String url,
			BasicNameValuePair... params) {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String log ="Invoking WS with params: ";
		
		for(BasicNameValuePair pair: params){
			log += pair.getName() + "=" + pair.getValue() + " // ";
			nameValuePairs.add(pair);
		}
		Log.i("WebService", log + " -------- URL is: " + url);
		
		String result = ""; // this string will store return data in JSON format
		
		InputStream is = null;
		
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent(); //fetch the answer
		} catch (Exception e) {
			Log.e("JSON", "Error in http connection " + e.toString());
			return null;
		}		
		//convert the returned data from InputStream to String
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("JSON", "Error converting result " + e.toString());
			return null;
		}
		
		Log.i("WebService", "Response is " + result);
		
		//the returned data from the web service:
		return result;
	}
}
