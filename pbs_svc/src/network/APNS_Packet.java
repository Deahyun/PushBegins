package network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import org.json.simple.JSONObject;

import utils.UserLog;

public class APNS_Packet {

	byte[] byPacket = null;
	int nPacketLength = 0;
	String tag = "APNS_Packet";
	
	public APNS_Packet() {
		byPacket = new byte[256];
	}
	
	@SuppressWarnings("unchecked")
	void putEx(JSONObject jo, String key, String value) {
		jo.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	void putEx(JSONObject jo, String key, int value) {
		jo.put(key, value);
	}
	
	public byte[] hexStringToByteArray(String str)
    {
		if ( str == null || str.length() == 0 )
			return null;
		//
		str = str.replace(" ", "");
		
		int nStrLength = str.length();
		if ( nStrLength != 64 )
			return null;

		//String strDebug  = String.format("%d", nStrLength);
		//UserLog.Log(tag, strDebug);
		
		byte[] buffer = new byte[nStrLength / 2];
        for (int i = 0; i < nStrLength/2; i++ ) {
            buffer[i] = (byte)Integer.parseInt(str.substring(i*2, i*2 + 2), 16);
            //strDebug = String.format("range %d -> %d", i*2, i*2 + 2);
            //UserLog.Log(tag, strDebug);
        }
        return buffer;
    }
	
	//
	public String byteArrayToHexString(byte[] bytes) {
	    StringBuilder result = new StringBuilder();
	    for (byte b : bytes) {
	    	String strFmt = String.format("%02x", b);
	    	result.append(strFmt);
	        //result.append(Integer.toHexString(b));
	    }
	    return result.toString();
	}
	
	public byte[] GetIntBytes(int value)
	{
	    //ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
	    buffer.putInt(value);
	    return buffer.array();
	}
	
	public byte[] GetShortBytes(short value)
	{
	    //ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.nativeOrder());
	    ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN);
	    buffer.putShort(value);
	    return buffer.array();
	}

    public void makePacket(String msg_id, String message, String device_token)
    {
        if (message == null || device_token == null)
        {
            UserLog.Log(tag, "message == null || device_token == null");
            return;
        }
        //
        int nPos = 0;
        //
        byte[] byCmd = new byte[1];
        byCmd[0] = 0;
        System.arraycopy(byCmd, 0, byPacket, 0, 1);
        nPos += 1;
        //
        short token_length = 32;
        byte[] byTokenLength = GetShortBytes(token_length);
        System.arraycopy(byTokenLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        byte[] byToken = hexStringToByteArray(device_token.toUpperCase());
        System.arraycopy(byToken, 0, byPacket, nPos, 32);
        nPos += 32;
        //
        byte[] byTmp = new byte[32];
		System.arraycopy(byToken, 0, byTmp, 0, 32);
		//String strDeviceToken = byteArrayToHexString(byTmp);
		//UserLog.Log(tag, strDeviceToken);
        
        //
        String payload = "{\"aps\":{\"alert\":\"" + message + "\",\"badge\":1,\"sound\":\"default\"},\"msg_id\":\"" + msg_id + "\"}";
        byte[] byData = null;
        try {
        	byData = payload.getBytes("UTF-8");
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
        //
        short payload_length = (short)byData.length;
        byte[] byPayloadLength = GetShortBytes(payload_length);
        System.arraycopy(byPayloadLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        System.arraycopy(byData, 0, byPacket, nPos, payload_length);
        nPos += payload_length;
        //
        int active_length = (token_length + payload_length + 5);
        nPacketLength = active_length;
        //
        //String strDebug = String.format("packet length: %d", nPacketLength);
        //UserLog.Log(tag, strDebug);
    }
    
    //
    public void makePacket(String message, String device_token)
    {
        if (message == null || device_token == null)
        {
            UserLog.Log(tag, "message == null || device_token == null");
            return;
        }
        //
        int nPos = 0;
        //
        byte[] byCmd = new byte[1];
        byCmd[0] = 0;
        System.arraycopy(byCmd, 0, byPacket, 0, 1);
        nPos += 1;
        //
        short token_length = 32;
        byte[] byTokenLength = GetShortBytes(token_length);
        System.arraycopy(byTokenLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        byte[] byToken = hexStringToByteArray(device_token.toUpperCase());
        System.arraycopy(byToken, 0, byPacket, nPos, 32);
        nPos += 32;
        //
        byte[] byTmp = new byte[32];
		System.arraycopy(byToken, 0, byTmp, 0, 32);
        //
        //String payload = "{\"aps\":{\"alert\":\"" + message + "\",\"badge\":1,\"sound\":\"default\"}}";
		String payload = "{\"aps\":{\"alert\":\"" + message + "\",\"badge\":4,\"sound\":\"default\"}}";
        byte[] byData = null;
        try {
        	byData = payload.getBytes("UTF-8");
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
        //
        short payload_length = (short)byData.length;
        byte[] byPayloadLength = GetShortBytes(payload_length);
        System.arraycopy(byPayloadLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        System.arraycopy(byData, 0, byPacket, nPos, payload_length);
        nPos += payload_length;
        //
        int active_length = (token_length + payload_length + 5);
        nPacketLength = active_length;
        //
        //String strDebug = String.format("packet length: %d", nPacketLength);
        //UserLog.Log(tag, strDebug);
    }
    
    @SuppressWarnings("unchecked")
	public boolean makePacket(APNS_Message message, String device_token)
    {
        if (message == null || device_token == null) {
            UserLog.Log(tag, "message == null || device_token == null");
            return false;
        }
        if (message.alert == null || message.alert.length() == 0 ) {
            UserLog.Log(tag, "message.alert has no value");
            return false;
        }
        ////
        final int nUTF8LimitLength = 200;
        try {
        	byte[] utf8_alert = message.alert.getBytes("UTF-8");
        	//byte[] utf8_user_data = message.user_data.getBytes("UTF-8");
			//if ( (utf8_alert.length + utf8_user_data.length) > nUTF8LimitLength ) {
        	if ( utf8_alert.length > nUTF8LimitLength ) {
	            UserLog.Log(tag, "message.alert has too long value");
	            return false;
			}
        } catch ( Exception e ) {
        	e.printStackTrace();
        	return false;
        }
        ////
        
        //
        int nPos = 0;
        //
        byte[] byCmd = new byte[1];
        byCmd[0] = 0;
        System.arraycopy(byCmd, 0, byPacket, 0, 1);
        nPos += 1;
        //
        short token_length = 32;
        byte[] byTokenLength = GetShortBytes(token_length);
        System.arraycopy(byTokenLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        byte[] byToken = hexStringToByteArray(device_token.toUpperCase());
        System.arraycopy(byToken, 0, byPacket, nPos, 32);
        nPos += 32;
        //
        byte[] byTmp = new byte[32];
		System.arraycopy(byToken, 0, byTmp, 0, 32);
        //
        //String payload = "{\"aps\":{\"alert\":\"" + message + "\",\"badge\":1,\"sound\":\"default\"}}";
		//String payload = "{\"aps\":{\"alert\":\"" + message.alert + "\",\"badge\":4,\"sound\":\"default\"}}";
		JSONObject joRoot = new JSONObject();
		JSONObject joAps = new JSONObject();
		//JSONObject joExtra = new JSONObject();
		
		//
		joAps.put("alert", message.alert);
		if ( message.badge > 0 )
			joAps.put("badge", message.badge);
		if ( message.sound != null && message.sound.length() > 0 ) {
			joAps.put("sound", message.sound);
		}
		//
		joAps.put("content-available", 1);
		//
		if ( message.user_data != null && message.user_data.length() > 0 ) {
			//joRoot.put("user_data", message.user_data);
			joRoot.put("user_data", message.m_id);
		}
		//
		joRoot.put("aps", joAps);
		
		//
		String payload = joRoot.toString();
		//UserLog.Log(tag, "payload -> " + payload);
		
		//
//		int nPayloadLength = 0;
//		//int nPayloadLength = payload.length();
//		byte[] utf8 = null;
//		try {
//			utf8 = payload.getBytes("UTF-8");
//			nPayloadLength = utf8.length;
//		} catch ( Exception e ) {
//			
//		}
		//UserLog.Log(tag, "payload length -> " + nPayloadLength);
//		if ( false && nPayloadLength > byPacket.length ) {
//			//String strAlert = message.alert;
//			//int nAlert = strAlert.length();
//			//strAlert = strAlert.substring(0, nPayloadLength - nAlert);
//			byte[] byPayload = new byte[nPayloadLength - byPacket.length];
//			System.arraycopy(utf8, 0, byPayload, 0, nPayloadLength - byPacket.length);
//			String strAlert = new String(utf8, 0, nPayloadLength - byPacket.length);
//			////
//			joRoot.clear();
//			joAps.clear();
//			joExtra.clear();
//			//
//			joAps.put("alert", strAlert);
//			if ( message.badge > 0 )
//				joAps.put("badge", message.badge);
//			if ( message.sound == null || message.sound.length() == 0 ) {
//				//
//			} else {
//				joAps.put("sound", message.sound);
//			}
//			//
//			joExtra.put("a", 1234);
//			joExtra.put("b", "가나다라");
//			joAps.put("json_data", joExtra);
//			
//			//
//			joRoot.put("aps", joAps);
//			////
//			payload = joRoot.toString();
//			UserLog.Log(tag, "payload2 -> " + payload);
//		}
		
        byte[] byData = null;
        try {
        	byData = payload.getBytes("UTF-8");
        } catch ( Exception e ) {
        	e.printStackTrace();
        	return false;
        }
        //
        short payload_length = (short)byData.length;
        byte[] byPayloadLength = GetShortBytes(payload_length);
        System.arraycopy(byPayloadLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        System.arraycopy(byData, 0, byPacket, nPos, payload_length);
        nPos += payload_length;
        //
        int active_length = (token_length + payload_length + 5);
        nPacketLength = active_length;
        //
        //String strDebug = String.format("packet length: %d", nPacketLength);
        //UserLog.Log(tag, strDebug);
        //
        return true;
    }
    
    //////////
    public void makePacketEx(String message, String device_token)
    {
        if (message == null || device_token == null)
        {
            UserLog.Log(tag, "message == null || device_token == null");
            return;
        }
        //
        int nPos = 0;
        //
        byte[] byCmd = new byte[1];
        byCmd[0] = 1;
        System.arraycopy(byCmd, 0, byPacket, 0, 1);
        nPos += 1;
        //
        int identifier = 1234;
        byte[] byIdentifier = GetIntBytes(identifier);
        System.arraycopy(byIdentifier, 0, byPacket, nPos, 4);
        nPos += 4;
        
        //
        Date now = new Date();
        long lExpiry = now.getTime();
        int expiry = (int)(lExpiry / 1000L) + 86400;
        byte[] byExpiry = GetIntBytes(expiry);
        //byExpiry[0] = byExpiry[1] = byExpiry[2] = byExpiry[3] = 0;
        System.arraycopy(byExpiry, 0, byPacket, nPos, 4);
        nPos += 4;
        
        //
        short token_length = 32;
        byte[] byTokenLength = GetShortBytes(token_length);
        System.arraycopy(byTokenLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        byte[] byToken = hexStringToByteArray(device_token.toUpperCase());
        System.arraycopy(byToken, 0, byPacket, nPos, 32);
        nPos += 32;
        //
        String payload = "{\"aps\":{\"alert\":\"" + message + "\",\"badge\":1,\"sound\":\"default\"}}";
        byte[] byData = null;
        try {
        	byData = payload.getBytes("UTF-8");
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
        //
        short payload_length = (short)byData.length;
        byte[] byPayloadLength = GetShortBytes(payload_length);
        System.arraycopy(byPayloadLength, 0, byPacket, nPos, 2);
        nPos += 2;
        //
        System.arraycopy(byData, 0, byPacket, nPos, payload_length);
        nPos += payload_length;
        //
        int active_length = (token_length + payload_length + 13);
        nPacketLength = active_length;
        //
        //String strDebug = String.format("packet_ex length: %d", nPacketLength);
        //UserLog.Log(tag, strDebug);
    }

    public byte[] getPacket()
    {
        byte[] byResult = new byte[nPacketLength];
        //Array.Copy(byPacket, byResult, nPacketLength);
        System.arraycopy(byPacket, 0, byResult, 0, nPacketLength);
        return byResult;
    }
}
