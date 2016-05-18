package com.entertera.pushbeginslib;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ImageSpan;

public class UserFunc {
	//
	public static char szDeli = 0x18;
	public static String strDeli = String.format("%c", szDeli);
	
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
	public static String now2Day() {
		Locale loc = Locale.KOREA;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", loc);
		Date aDate = new Date();
		String strTime = sdf.format(aDate);
		return strTime;
	}
	
	//
	public static String getNowTime() {
		Locale loc = Locale.KOREA;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", loc);
		Date aDate = new Date();
		String strTime = sdf.format(aDate);
		return strTime;
	}

	//
	public static int now2IntTime() {
		return (int)(System.currentTimeMillis() / 1000L);
	}
	
	//
	public static long now2LongTime() {
		return System.currentTimeMillis();
	}
	
	//
	public static String getDeltaTime(int nLastTime) {
		int nDeltaSeconds = now2IntTime() - nLastTime;
		//
		int nDeltaMinutes = nDeltaSeconds / 60;
		if ( nDeltaMinutes == 0 ) 
			return  nDeltaSeconds + "초전";
		
		int nDeltaHours = nDeltaMinutes / 60;
		if ( nDeltaHours == 0 ) 
			return  nDeltaMinutes + "분전";
		
		int nDeltaDays = nDeltaHours / 24;
		if ( nDeltaDays == 0 ) 
			return  nDeltaHours + "시간전";
		
		int nDeltaWeeks = nDeltaDays / 7;
		if ( nDeltaWeeks == 0 ) 
			return  nDeltaDays + "일전";
		
		int nDeltaMonths = nDeltaDays / 30;
		if ( nDeltaMonths == 0 ) 
			return  nDeltaWeeks + "주전";
		
		return nDeltaMonths + "달전";
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
	
	//
	public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue( HashMap<K, V> map ) {
		List<HashMap.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o1.getValue()).compareTo( o2.getValue() );
			}
				} );

		HashMap<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	
	//
	public static boolean isScreenOn(Context context) {
		return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
	}
	
	//
	public static String getExternalStorageDirectory2() {
		//return "/mnt/sdcard";
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	//
	public static String getAssetsPath(String strFilePath) {
		String strResult = String.format("file:///android_asset/%s", strFilePath);
		return strResult;
	}

	// uri -> path
	public static String getPathFromUri(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null );
		cursor.moveToNext(); 
		String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
		cursor.close();
		return path;
	}

	// path -> uri
	public static Uri getUriFromPath(Context context, String strFilePath) {
		Uri fileUri = Uri.parse( strFilePath );
		String filePath = fileUri.getPath();
		Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
				null, "_data = '" + filePath + "'", null, null );
		cursor.moveToNext();
		int id = cursor.getInt( cursor.getColumnIndex( "_id" ) );
		Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );
		return uri;
	}
	
	////
	public static SpannableString getEmoticonString(Context context, String text) {
 		SpannableString result = new SpannableString(text);
		if ( true ) 
			return result;
		// for emoticon...
		Pattern pattern = Pattern.compile("\\((.*?)\\)"); // 괄호 안의 문자와 매칭
		Matcher match = pattern.matcher(text);
		
		while ( match.find() ) {
			int start = match.start();
			int end = match.end();

			String tmp = match.group();
			
			// 두개의 괄호 (( 를 하나의 괄호 ( 로
			while ( tmp.contains("((") ) {
				start = start + tmp.indexOf("(") + 1;
				tmp = tmp.substring(tmp.indexOf("(") + 1);
			}
			
			//String strDebug = String.format("%d %d %s", start, end, tmp);
			//Log.i(tag, strDebug);

//			Integer nValue = em_map.get(tmp);
//			if ( nValue == null || nValue == 0 ) {
//				continue;
//			}
			Integer nValue = 1; 
			Drawable drawable = context.getResources().getDrawable(nValue);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			// ImageSpan을 이용한 이미지 변경
			result.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return result;
	}
	
	////////
}
