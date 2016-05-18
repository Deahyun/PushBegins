package com.entertera.pushbeginslib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

public class UserHttpClient {

	HttpURLConnection conn = null;
	OutputStream os = null;
	InputStream is = null;
	ByteArrayOutputStream baos = null;
	
	//
	MiniPushObject obj = MiniPushObject.getInstance();
	//String strServerAddress = "http://192.168.0.103:8080/pbs_svc/Listener";
	String strServerAddress = obj.web_url;
	
	//
	boolean bDebug = true;
	String tag = "UserHttpClient";
	String strDebug;
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// Singleton class
	private static UserHttpClient m_instance = new UserHttpClient();
	private UserHttpClient() { }
	public static UserHttpClient getInstance() {
		return m_instance;
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	
	private class asyncJsonRequest extends AsyncTask<String, Void, String> {

		ProgressDialog mProgress;
		Looper mLoop;
		//
		String aTag = "asyncJsonRequest";
		@Override 
		protected void onPreExecute() {
			super.onPreExecute();
			//
		}
		
		@Override
		protected String doInBackground(String... strParams) {
			//
			String strUrl = strParams[0];
			String strJsonData = strParams[1];
			//
			String strResult = syncJsonRequest(strUrl, strJsonData);
			return strResult;
		}
		
		@Override 
		protected void onProgressUpdate(Void... vParam) {
			//
		}
		
		@Override 
		protected void onPostExecute(String result) {
			//Log.i(aTag, result);
		}

		@Override 
		protected void onCancelled() { 
			// TODO Auto-generated method stub 
			super.onCancelled(); 
		}
	}
	
	//
	public String jsonRequest(String strUrl, String strJsonData) {
		String strResult = null;
		try {
			String[] strParams = { strUrl, strJsonData };
			strResult = new asyncJsonRequest().execute(strParams).get();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return strResult;
	}

	//public synchronized String jsonRequest(String strUrl, String strJsonData) {
	//public String jsonRequest(String strUrl, String strJsonData) {
	public String syncJsonRequest(String strUrl, String strJsonData) {
		
		String strResult = null;

		if ( bDebug ) {
			strDebug = String.format("%s -> %s", strUrl, strJsonData);
			Log.i(tag, strDebug);
		}
		//////////
		try {
			// URL 설정
			URL url = new URL(strUrl);
			conn = (HttpURLConnection)url.openConnection();
			
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20 * 1000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "utf-8"); 
			PrintWriter writer = new PrintWriter(outStream); 
			writer.write(strJsonData); 
			writer.flush();
			//
			writer.close();
			outStream.close();

			int responseCode = conn.getResponseCode();
			if ( responseCode != HttpURLConnection.HTTP_OK ) {
				String strCode = String.format("response code: %d", responseCode);
				Log.e(tag, strCode);
			}

			if ( responseCode == HttpURLConnection.HTTP_OK ) {

				InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");  
				BufferedReader reader = new BufferedReader(isr); 
				StringBuilder builder = new StringBuilder(); 
				String str = null;
				while ( (str = reader.readLine()) != null ) { 
					builder.append(str);
				}
				
				if ( bDebug ) {
					Log.i(tag, "jsonRequest result -> " + builder.toString());
				}
				//
				reader.close();
				isr.close();
				//
				strResult = builder.toString();
				
			} else {
				Log.e(tag, "HTTP Response not OK");
			}
			//
			conn.disconnect();
			conn = null;

//		} catch (MalformedURLException e) { 
//			// 
//			e.printStackTrace();
//			return null;
			
		} catch (IOException e) { 
			//  
			e.printStackTrace();
			return null;
			
		} // try
		//////////
		
		return strResult;
	}
	
	//
	// device_id 는 email, phone, cid 중 하나
	public int registerDevice(GcmDeviceInfo info) {
		//MiniPushObject obj = MiniPushObject.getInstance();
		//String strUrl  = String.format("%s", obj.web_url);
		String strUrl  = String.format("%s", strServerAddress);
		if ( bDebug ) {
			Log.i(tag, "registerDevice -> " + strServerAddress);
		}
		//
		int nDeviceSeq = 0;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "update_gcm_device_info_req");
			req.put("device_id", info.device_id);
			req.put("device_token", info.device_token);
			req.put("proj_num", info.proj_num);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return 0;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return 0;
			}
			String strDeviceSeq = jo.getString("device_seq").toString();
			nDeviceSeq = Integer.parseInt(strDeviceSeq);

		} catch ( Exception e ) {
			e.printStackTrace();
			return 0;
		}

		return nDeviceSeq;
	}
	
	//
	public boolean verifyMessageId(long message_id) {
		//
		//MiniPushObject obj = MiniPushObject.getInstance();
		//String strUrl  = String.format("%s", obj.web_url);
		String strUrl  = String.format("%s", strServerAddress);
		//
		boolean bResult = false;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "verify_message_id_req");
			req.put("m_id", message_id);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return bResult;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return bResult;
			}
			//strResult = jo.getString("user_id").toString();
			bResult = true;

		} catch ( Exception e ) {
			e.printStackTrace();
			return bResult;
		}

		return bResult;
	}
	
	// device_seq = MiniPushObject.getInstance().getValue(DbDef.DEVICE_SEQ)
	public boolean confirmMessage(long message_id, int device_seq) {
		//
		//MiniPushObject obj = MiniPushObject.getInstance();
		//String strUrl  = String.format("%s", obj.web_url);
		String strUrl  = String.format("%s", strServerAddress);
		//
		boolean bResult = false;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "confirm_message_req");
			req.put("m_id", message_id);
			req.put("device_seq", device_seq);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return bResult;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return bResult;
			}
			//strResult = jo.getString("user_id").toString();
			bResult = true;

		} catch ( Exception e ) {
			e.printStackTrace();
			return bResult;
		}

		return bResult;
	}
	
	//
	public boolean sendCustomerMessage(int nDeviceSeq, String strProjectNumber, String strMessage) {
		String strUrl  = String.format("%s", strServerAddress);
		//
		boolean bResult = false;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "send_customer_message_a_req");
			req.put("device_seq", nDeviceSeq);
			req.put("proj_num", strProjectNumber);
			req.put("message", strMessage);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return bResult;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return bResult;
			}
			//strResult = jo.getString("user_id").toString();
			bResult = true;

		} catch ( Exception e ) {
			e.printStackTrace();
			return bResult;
		}

		return bResult;
	}
	
	
	//////
	public String getConnectAddress(String strProjNumber) {
		MiniPushObject obj = MiniPushObject.getInstance();
		String strUrl  = String.format("%s", obj.master_url);
		//
		String strRes = null;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "get_android_address_req");
			req.put("proj_num", strProjNumber);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return strRes;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return strRes;
			}
			strRes = jo.getString("address").toString();

		} catch ( Exception e ) {
			e.printStackTrace();
			return strRes;
		}

		return strRes;
	}
	
	//
	public void setServerAddress(String address) {
		strServerAddress = address;
		if ( bDebug )
			Log.i(tag, "setServerAddress -> " + strServerAddress);
	}
	
	//
	public String getUserId(String strDeviceToken) {
		//
		//MiniPushObject obj = MiniPushObject.getInstance();
		//String strUrl  = String.format("%s", obj.web_url);
		String strUrl  = String.format("%s", strServerAddress);
		//
		String strUserId = null;
		//
		JSONObject req = new JSONObject();
		try {
			//
			req.put("io_kind", "get_gcm_user_id_req");
			req.put("device_token", strDeviceToken);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		//
		String strResponse = jsonRequest(strUrl, req.toString());
		if ( strResponse == null || strResponse.length() == 0 ) {
			return strUserId;
		}
		if ( bDebug )
			Log.i(tag, strResponse);
		
		// parse response
		JSONObject jo = null;

		try {
			jo = new JSONObject(strResponse);

			//String strIoKind = jo.getString("io_kind").toString();
			String strResult = jo.getString("result").toString();
			if ( strResult.compareTo("yes") != 0 ) {
				return strUserId;
			}
			strUserId = jo.getString("user_id").toString();

		} catch ( Exception e ) {
			e.printStackTrace();
			return strUserId;
		}

		return strUserId;
	}
	//

	////
}

//  Usage example:
//	UserHttpClient client = UserHttpClient.getInstance();
//	if ( !client.editProfile(info) ) {
//		Toast.makeText(ctx, "사용자 정보변경시 서버 오류입니다.", Toast.LENGTH_SHORT).show();
//		return;
//	} 



