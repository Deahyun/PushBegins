package network;

import java.util.ArrayList;
import java.util.Vector;

import utils.UserLog;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

public class GCMClient {

	String tag = "GCM";
	private String api_key;
	private Sender sender;
	private ArrayList<String> devicesList;
	private ArrayList<String> curDevicesList;
	
	public void setApiKey(String key) {
		if ( key.length() == 0 )
			return;
		
		api_key = key;
		//
		sender = new Sender(api_key);
	}
	
	public void setDeviceList(Vector<String> vDeviceList) {
		if ( devicesList == null ) {
			devicesList = new ArrayList<String>();
			int nItems = vDeviceList.size();
			for ( int i = 0; i < nItems; i++ ) {
				devicesList.add(vDeviceList.elementAt(i));
			}
		}
		//
		if ( curDevicesList == null ) {
			curDevicesList = new ArrayList<String>();
		}
	}
	
	//public void sendGCM(String strMsgId, String strJson) {
	public int sendGCM(String strJson, long message_id) {
		//
		int nResult = 0;
		String strMsgId = Long.toString(message_id);
		//
		Message message = new Message.Builder()
		//.collapseKey("1")
		.timeToLive(86400)
		.delayWhileIdle(false)
		.addData("json_data", strJson)
		.addData("m_id", strMsgId)
		.build();		
		
		for ( int i = 0; i < devicesList.size(); i++ ) {
			curDevicesList.add(devicesList.get(i));
			if ( i % 901 == 900 ) {
				//
				nResult += sendMessage(message, curDevicesList);
				//
				curDevicesList.clear();
			}
			//
		}
		//
		if ( curDevicesList.size() > 0 ) {
			nResult += sendMessage(message, curDevicesList);
		}
		
		return nResult;
	}
	
	//
	public int sendMessage(Message message, ArrayList<String> devicesList) {
		//
		int nResult = 0;
		//
		try {
			int nRetry = 1;
			MulticastResult result = sender.send(message, devicesList, nRetry);
			if ( result.getResults() != null ) {
				int canonicalRegId = result.getCanonicalIds();
				//String strDebug = String.format("canonicalRegId: %d %s", canonicalRegId, result.toString());
				//UserLog.Log(tag, strDebug);
				if ( canonicalRegId != 0 ) {
					// success ?
					//UserLog.Log(tag, "Success");
					nResult = result.getSuccess();
					
				} else {
					// failure 1
					//UserLog.Log(tag, "Failure 1");
					nResult = result.getSuccess();
				}
			} else {
				// failure 2
				int error = result.getFailure();
				UserLog.Log(tag, "Failure 2: " + error);
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return nResult;
	}
}
