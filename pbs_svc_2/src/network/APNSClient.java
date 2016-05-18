package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import utils.UserLog;

public class APNSClient {

	String tag = "APNS";
	String strDebug;
	
	private int port = 2195;
	private int feedback_port = 2196;
	private String hostname = "gateway.sandbox.push.apple.com";
	private String feedback_host = "feedback.sandbox.push.apple.com";
	private String certPath, password;
	
	public void setSandboxMode(boolean bSandBox) {
		if ( bSandBox ) {
			hostname = "gateway.sandbox.push.apple.com";
			feedback_host = "feedback.sandbox.push.apple.com";
		} else {
			hostname = "gateway.push.apple.com";
			feedback_host = "feedback.push.apple.com";
		}
	}
	
	public void setCertPath(String path) {
		certPath = path;
	}
	
	public void setPwd(String pwd) {
		password = pwd;
	}
	
	public boolean isValid() {
		if ( certPath == null || certPath.length() == 0 ) {
			UserLog.Log(tag, "isValid() -> certPath false");
			return false;
		}
		if ( password == null || password.length() == 0 ) {
			UserLog.Log(tag, "isValid() -> password false");
			return false;
		}
		return true;
	}

	public void push(String strMsgId, String strMessage, String strDeviceToken) {

		if ( !isValid() ) return;
		//
		char[] passwd = password.toCharArray();
		//
		BufferedOutputStream bos;
		BufferedInputStream bin;
		SSLSocket socket = null;
		//
		try {
			//
			APNS_Packet packet = new APNS_Packet();
			packet.makePacket(strMsgId, strMessage, strDeviceToken);
			//packet.makePacketEx(strMessage, strDeviceToken);
			byte[] bySend = packet.getPacket();
			//

			//
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream(certPath), passwd);
			KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ts, passwd);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(tmf.getKeyManagers(), null, null);
			SSLSocketFactory factory = sslContext.getSocketFactory();

			socket = (SSLSocket)factory.createSocket(hostname, port); 
			String[] suites = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(suites);
			//start handshake
			socket.startHandshake();
			//THIS ALWAYS RETURNS FALSE -> ignore return value
			//boolean connected = socket.isConnected();

			//
			bos = new BufferedOutputStream(socket.getOutputStream());
			bos.write(bySend);
			bos.flush();

			// Close connection.
			bos.close();
			//bin.close();
			socket.close();

		} catch ( Exception e ) {
			e.printStackTrace();
			//
			try {
				byte[] byRecv = new byte[6];
				bin = new BufferedInputStream(socket.getInputStream());
				int nRead = bin.read(byRecv);
				//String strDebug = String.format("Recv: %d", nRead);
				//UserLog.Log(tag, strDebug);
				//
				if ( false ) {
					for ( int i = 0; i < 6; i++ ) {
						String strTmp = String.format("%02x ", byRecv[i]);
						UserLog.Log(tag, strTmp);
					}
				}
			} catch ( Exception e2 ) {
				//
			}
		}
		//
	}

	public void push_multi(String strMsgId, String strMessage, Vector<String> vDeviceToken) {
		if ( !isValid() ) return;
		//
		char[] passwd = password.toCharArray();
		//
		BufferedOutputStream bos;
		BufferedInputStream bin;
		SSLSocket socket = null;
		//
		try {
			//
			String strDebug = String.format("%s %s", certPath, password);
			UserLog.Log(tag, strDebug);
			//
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream(certPath), passwd);
			KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ts, passwd);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(tmf.getKeyManagers(), null, null);
			SSLSocketFactory factory = sslContext.getSocketFactory();

			socket = (SSLSocket)factory.createSocket(hostname, port); 
			String[] suites = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(suites);
			//start handshake
			socket.startHandshake();
			//THIS ALWAYS RETURNS FALSE -> ignore return value
			//boolean connected = socket.isConnected();

			//
			bos = new BufferedOutputStream(socket.getOutputStream());
			//
			//
			//for ( String deviceToken : listDeviceToken ) {
			for ( int i = 0; i < vDeviceToken.size(); i++ ) {
				APNS_Packet packet = new APNS_Packet();
				packet.makePacket(strMsgId, strMessage, vDeviceToken.get(i));
				//packet.makePacketEx(strMessage, vDeviceToken.get(i));
				byte[] bySend = packet.getPacket();
				//
				bos.write(bySend);
				// 900 건에 한번씩 force writing
				if ( i % 901 == 900 ) {
					bos.flush();
				}
			}
			//
			bos.flush();
			//
			bos.close();
			socket.close();

		} catch ( Exception e ) {
			e.printStackTrace();
			//
			if ( true ) {
				try {
					byte[] byRecv = new byte[6];
					bin = new BufferedInputStream(socket.getInputStream());
					int nRead = bin.read(byRecv);
					//String strDebug = String.format("Recv: %d", nRead);
					//UserLog.Log(tag, strDebug);
					//
					if ( false ) {
						for ( int i = 0; i < 6; i++ ) {
							String strTmp = String.format("%02x ", byRecv[i]);
							UserLog.Log(tag, strTmp);
						}
					}
				} catch ( Exception e2 ) {
					//
				}
			}
		}
		//
	}
	
	//
	public void push_multi(String strMessage, Vector<String> vDeviceToken) {
		if ( !isValid() ) return;
		//
		char[] passwd = password.toCharArray();
		//
		BufferedOutputStream bos;
		BufferedInputStream bin;
		SSLSocket socket = null;
		//
		try {
			//
			//String strDebug = String.format("%s %s", certPath, password);
			//UserLog.Log(tag, strDebug);
			//
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream(certPath), passwd);
			KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ts, passwd);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(tmf.getKeyManagers(), null, null);
			SSLSocketFactory factory = sslContext.getSocketFactory();

			socket = (SSLSocket)factory.createSocket(hostname, port); 
			String[] suites = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(suites);
			//start handshake
			socket.startHandshake();
			//THIS ALWAYS RETURNS FALSE -> ignore return value
			//boolean connected = socket.isConnected();

			//
			bos = new BufferedOutputStream(socket.getOutputStream());
			//
			//
			//for ( String deviceToken : listDeviceToken ) {
			for ( int i = 0; i < vDeviceToken.size(); i++ ) {
				APNS_Packet packet = new APNS_Packet();
				packet.makePacket(strMessage, vDeviceToken.get(i));
				//packet.makePacketEx(strMessage, vDeviceToken.get(i));
				byte[] bySend = packet.getPacket();
				//
				bos.write(bySend);
				// 900 건에 한번씩 force writing
				if ( i % 901 == 900 ) {
					bos.flush();
				}
			}
			//
			bos.flush();
			//
			bos.close();
			socket.close();

		} catch ( Exception e ) {
			e.printStackTrace();
			//
			if ( true ) {
				try {
					byte[] byRecv = new byte[6];
					bin = new BufferedInputStream(socket.getInputStream());
					int nRead = bin.read(byRecv);
					//String strDebug = String.format("Recv: %d", nRead);
					//UserLog.Log(tag, strDebug);
					//
					if ( false ) {
						for ( int i = 0; i < 6; i++ ) {
							String strTmp = String.format("%02x ", byRecv[i]);
							UserLog.Log(tag, strTmp);
						}
					}
				} catch ( Exception e2 ) {
					//
				}
			}
		}
		//
	}
	
	
	//
	public int push_multi(APNS_Message message, Vector<String> vDeviceToken) {
		if ( !isValid() ) return 0;
		//
		strDebug = "pwd: " + password;
		UserLog.Log(tag, strDebug);
		//
		int nResult = 0;
		//
		char[] passwd = password.toCharArray();
		//
		BufferedOutputStream bos;
		BufferedInputStream bin;
		SSLSocket socket = null;
		//
		try {
			//
			//String strDebug = String.format("%s %s", certPath, password);
			//UserLog.Log(tag, strDebug);
			//
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream(certPath), passwd);
			KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ts, passwd);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(tmf.getKeyManagers(), null, null);
			SSLSocketFactory factory = sslContext.getSocketFactory();

			socket = (SSLSocket)factory.createSocket(hostname, port);
			String[] suites = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(suites);
			//start handshake
			socket.startHandshake();
			//THIS ALWAYS RETURNS FALSE -> ignore return value
			//boolean connected = socket.isConnected();

			boolean bRes;
			bos = new BufferedOutputStream(socket.getOutputStream());
			//
			//
			//for ( String deviceToken : listDeviceToken ) {
			for ( int i = 0; i < vDeviceToken.size(); i++ ) {
				//strDebug = " token: " + vDeviceToken.get(i);
				//UserLog.Log(tag, strDebug);
				
				APNS_Packet packet = new APNS_Packet();
				bRes = packet.makePacket(message, vDeviceToken.get(i));

				if ( !bRes ) break;
				//packet.makePacket(strMessage, vDeviceToken.get(i));
				//packet.makePacketEx(strMessage, vDeviceToken.get(i));
				byte[] bySend = packet.getPacket();
				//
				bos.write(bySend);
				nResult++;
				// 900 건에 한번씩 force writing
				if ( i % 901 == 900 ) {
					bos.flush();
				}
			}
			//
			bos.flush();
			//
			bos.close();
			socket.close();

		} catch ( Exception e ) {
			UserLog.Log(tag, "push_multi error -> " + e.getMessage());
			e.printStackTrace();
			//
			if ( false ) {
				try {
					byte[] byRecv = new byte[6];
					bin = new BufferedInputStream(socket.getInputStream());
					int nRead = bin.read(byRecv);
					//String strDebug = String.format("Recv: %d", nRead);
					//UserLog.Log(tag, strDebug);
					//
					if ( false ) {
						for ( int i = 0; i < 6; i++ ) {
							String strTmp = String.format("%02x ", byRecv[i]);
							UserLog.Log(tag, strTmp);
						}
					}
				} catch ( Exception e2 ) {
					//
				}
			}
		}
		return nResult;
		//
	}
	
	//
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
	
	// feedback format
	// <time_t(4) + token_length(2) + device_token(32)> + <...> + <...> + ...
	public Vector<String> feedback() {
		Vector<String> vResult = new Vector<String>();
		//
		if ( !isValid() ) return vResult;
		//
		char[] passwd = password.toCharArray();
		//
		BufferedOutputStream bos;
		BufferedInputStream bin;
		SSLSocket socket = null;
		//
		try {
			//
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream(certPath), passwd);
			KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ts, passwd);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(tmf.getKeyManagers(), null, null);
			SSLSocketFactory factory = sslContext.getSocketFactory();

			socket = (SSLSocket)factory.createSocket(feedback_host, feedback_port); 
			String[] suites = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(suites);
			//start handshake
			socket.startHandshake();
			//THIS ALWAYS RETURNS FALSE -> ignore return value
			//boolean connected = socket.isConnected();
			//
			byte[] byRecv = new byte[1000];
			bin = new BufferedInputStream(socket.getInputStream());
			int nRead = bin.read(byRecv);
			//String strDebug = String.format("Recv: %d", nRead);
			//UserLog.Log(tag, strDebug);
			if ( false ) {
				for ( int i = 0; i < nRead; i++ ) {
					strDebug = String.format("0x%02X ", byRecv[i]);
					UserLog.Log(tag, strDebug);
				}
			}
			if ( nRead == -1 ) {
				return vResult;
			}
			int nCount = nRead / 38;
			for ( int i = 0; i < nCount; i++ ) {
				byte[] byTmp = new byte[32];
				System.arraycopy(byRecv, 6, byTmp, 0, 32);
				String strDeviceToken = byteArrayToHexString(byTmp);
				vResult.add(strDeviceToken);
				UserLog.Log(tag, "count:" + i + " -> " + strDeviceToken);
			}
			
			bin.close();
			socket.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		//
		return vResult;
	}
	
	////
}
