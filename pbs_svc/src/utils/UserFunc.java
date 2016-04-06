package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class UserFunc {
	//
	public static char szDeli = 0x18;
	public static String strDeli = String.format("%c", szDeli);
	//
	static int nPushMessageSeq = 0;
	final static int nMaxPushMessageSeq = 10000;

	//
	public static void Sleep(int nMiliSeconds) {
		try {
			Thread.sleep(nMiliSeconds);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	//
	//
	public static String intTime2String(int nTime) {
		Locale loc = Locale.KOREA;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", loc);
		Date aDate = new Date(nTime * 1000L);
		String strTime = sdf.format(aDate);
		return strTime;
	}

	//
	public static int now2IntTime() {
		return (int)(System.currentTimeMillis() / 1000L);
	}
	
	//
	public static String getTime() {
		Locale loc = Locale.KOREA;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", loc);
		Date aDate = new Date(System.currentTimeMillis());
		String strTime = sdf.format(aDate);
		return strTime;
	}
	
	//
	public static String getDate() {
		Locale loc = Locale.KOREA;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", loc);
		Date aDate = new Date(System.currentTimeMillis());
		String strTime = sdf.format(aDate);
		return strTime;
	}
	
	//
	public static long make_message_id() {
		long lResult = 0;
		nPushMessageSeq++;
		if ( nPushMessageSeq >= nMaxPushMessageSeq )
			nPushMessageSeq = 0;
		
		lResult = (now2IntTime() * 10000L) + nPushMessageSeq;
		
		return lResult;
	}
	
	//
	public static String joinString(Vector<String> vString) {
		StringBuilder sb = new StringBuilder();
		//
		for ( int i = 0; i < vString.size(); i++ ) {
			sb.append(vString.get(i));
			if ( i < vString.size() - 1 ) {
				sb.append(szDeli);
			}
		}
		return sb.toString();
	}

	//
	public static Vector<String> splitString(String strSrc) {
		String[] arrResult = strSrc.split(strDeli);
		//
		Vector<String> vString = new Vector<String>();		
		for ( int i = 0; i < arrResult.length; i++ ) {
			vString.add(arrResult[i]);
		}
		return vString;
	}
}
