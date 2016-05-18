package com.entertera.pushbeginslib;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneInfo {
		
	public static String getMacAddress(Context context) {
    	String macAddress = null;
    	boolean bWifiOff = false;
    	WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	if ( !wm.isWifiEnabled() ) {
    		wm.setWifiEnabled(true);
    		bWifiOff = true;
    	}
    	
    	WifiInfo wi = wm.getConnectionInfo();
    	macAddress = wi.getMacAddress();
    	
    	if ( bWifiOff ) {
    		wm.setWifiEnabled(false);
    		bWifiOff = false;
    	}
    	
    	macAddress = macAddress.replace(":", "");
    	
    	return macAddress;
    }
	
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if ( tm.getSimSerialNumber() == null || tm.getLine1Number() == null ) {
			Log.i("phone", "TelephonyManager failure");
			return null;
		}
//		String strRes = String.format("%s-%s",
//				tm.getSimSerialNumber().substring(2, 4), 
//				tm.getLine1Number());

		//tm.getNetworkCountryIso();
		String strRes = String.format("%s", tm.getLine1Number());
		
		// - 19자리 숫자코드 중 
		// 예) 89 82 05 1200 00 320451 0
	    // 첫두자리 통신사 ID, 그다음 두자리 국가코드, 그다음 두자리 네트워크 코드, 그다음 13자리 USIM번호
		// 89 82 05 1112602010441
		//strRes = tm.getSimSerialNumber();
		//tm.getNetworkCountryIso(), tm.getSimCountryIso()
		return strRes;
	}
	
	public static String getMCC(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    String networkOperator = tm.getNetworkOperator();
	    
	    if ( networkOperator == null ) {
	    	return "001";
	    }
	    return networkOperator.substring(0, 3);

//	    if (networkOperator != null) {
//	        int mcc = Integer.parseInt(networkOperator.substring(0, 3));
//	        int mnc = Integer.parseInt(networkOperator.substring(3));
//	    }
	}
	
	public static String getISONumber(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if ( tm.getSimSerialNumber() == null || tm.getLine1Number() == null ) {
			Log.i("phone", "TelephonyManager failure");
			return null;
		}

		String strRes = tm.getSimSerialNumber().substring(2, 4);
		return strRes;
	}
	
	//
	public static ArrayList<ContactInfo> getContactList(Context context) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.TYPE };

		String[] selectionArgs = null;

		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		ArrayList<ContactInfo> contactlist = new ArrayList<ContactInfo>();
		Cursor contactCursor = null;
		try {
			contactCursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
			if ( !contactCursor.moveToFirst() ) {
				return contactlist;
			}
			while ( !contactCursor.isAfterLast() ) {
				//
				String phonenumber = contactCursor.getString(1);
				phonenumber = phonenumber.replaceAll("-"," ");
				phonenumber = phonenumber.replaceAll(" ", "");
				// +8210xxx -> 010xxx 로 변경
				String strISONumber = "+" + PhoneInfo.getISONumber(context);
	    		String strHeaderNumber = phonenumber.substring(0, 3);
	    		if ( strHeaderNumber.compareTo(strISONumber) == 0 ) {
	    			//Log.i(tag, strHeaderNumber + " / " + phonenumber);
	    			phonenumber = phonenumber.replace(strISONumber, "0");
	    		}
				//
				ContactInfo acontact = new ContactInfo();
				acontact.photoid = contactCursor.getLong(0);
				acontact.phonenum = phonenumber;
				acontact.name = contactCursor.getString(2);
				acontact.type = contactCursor.getString(3);

				contactlist.add(acontact);
				//
				contactCursor.moveToNext();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( contactCursor != null ) {
				contactCursor.close();
			}
		}
		return contactlist;
	}
}
