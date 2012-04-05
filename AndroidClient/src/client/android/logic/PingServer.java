package client.android.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import client.android.gui.LoginTab;
import client.android.gui.PrefsTab;

public class PingServer extends AsyncTask<String, Integer, Long> {

    private static final int timeOut = 5000;
	
	@Override
	protected Long doInBackground(String... params) {	
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.e("other", e.getMessage());
		}
		
		String host = params[0];
		Log.i("ping", "Host is " + host);
		long time1;
		boolean status = false;
		
        time1 = System.currentTimeMillis();

        try {
            status = InetAddress.getByName(host).isReachable(timeOut);
        } catch (UnknownHostException ex) {
            Log.e("ping", ex.getMessage());
        } catch (IOException ex) {
        	Log.e("ping", ex.getMessage());
        }              
        
        if(status == true) return System.currentTimeMillis() - time1;
        else return -1L;
            
		
	}

	@Override
	protected void onPostExecute(Long result) {
		String text;
		if(result > 0){
			text = Long.toString(result) + " ms";
			LoginTab.pingTextView.setText(text);
			if(result <100L) LoginTab.pingTextView.setTextColor(Color.GREEN);
			else if(result >= 100L && result < 200L) LoginTab.pingTextView.setTextColor(Color.YELLOW);
			else LoginTab.pingTextView.setTextColor(Color.RED);					
		}
		else {
			text = "Time limit exceeded";
			LoginTab.pingTextView.setText(text);
			LoginTab.pingTextView.setTextColor(Color.RED);	
		}

		Log.i("ping", text);	
		
		//reinitialize the thread:
		new PingServer().execute(PrefsTab.serverIp);
	}

	@Override
	protected void onPreExecute() {

	}
}
