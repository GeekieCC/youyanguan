package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;

public class NetworkConnectUtil {

	public static final int CONNECT_TIME_OUT = 6000;
	public static final int READ_TIME_OUT = 6000;

	public static boolean isConnected(Context context) {
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		if(!isConnected)
			Toast.makeText(context, R.string.internet_not_connected, Toast.LENGTH_SHORT)
					.show();
		return isConnected;
	}
}
