package com.entertera.pushbeginslib;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyNetworkInfo {
	 
    public static boolean IsWifiAvailable(Context context)
    {
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;
 
            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            bConnect = (info.isAvailable() && info.isConnected());
 
        }
        catch (Exception e)
        {
        	Log.e("mynetwork", e.toString());
            return false;
        }
 
        return bConnect;
    }
 
    public static boolean Is3GAvailable(Context context)
    {  
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;
            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            bConnect = (info.isAvailable() && info.isConnected());
            //String strDebug = String.format("%s / %s",  info.getSubtype(), info.getSubtypeName());
            //Log.i("network", strDebug);
        }
        catch (Exception e)
        {
        	Log.i("mynetwork", e.toString());
            return false;
        }
 
        return bConnect;
    }
    
    
    public static boolean checkGPS(Context ctx) {
    	//String gps = android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    	 
    	LocationManager lm = (LocationManager)ctx.getSystemService(ctx.LOCATION_SERVICE);
    	boolean bEnableGPS = lm.isProviderEnabled (LocationManager.GPS_PROVIDER);
    	boolean bEnableWifiSearch = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	return (bEnableGPS || bEnableWifiSearch);
//    	if(isGPS) {
//    		return true;
//    	}
//    	else {
//    		Toast.makeText(액티비티이름.this, "GPS 사용을 체크해주세요 .", Toast.LENGTH_LONG).show();
//    		startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
//    	}
//    	return false;
    }

}