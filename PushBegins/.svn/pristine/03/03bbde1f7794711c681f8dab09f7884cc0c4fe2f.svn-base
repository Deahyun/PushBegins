package com.entertera.pushbegins;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.entertera.pushbeginslib.MiniPushObject;
import com.entertera.pushbeginslib.PushMsgInfo;
import com.entertera.pushbeginslib.UserFunc;
import com.google.android.gcm.GCMBaseIntentService;

@SuppressLint("Wakelock")
public class GCMIntentService extends GCMBaseIntentService {

	public static final int PUSH_RECEIVER_NOTI = 1;
	String tag = "GCMIntentService";
	Handler mHandler;
	String strDebug;
	Context ctx = null;
	
	public GCMIntentService() {
		//super(MiniPushObject.getInstance().getProjectNumber());
	}
	
	// For use of Toast
	public void onCreate() {
		super.onCreate();
		ctx = this;
		mHandler = new Handler();
	}

	@Override
	protected void onError(Context context, String regId) {
		// TODO Auto-generated method stub
		Log.e(tag, "error registration id : " + regId);
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		handleMessage(context, intent);
		//if ( intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE") ) {
		//	handleMessage(context, intent);
		//}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		// TODO Auto-generated method stub
		Log.i(tag, "onRegistered -> registration : " + regId);
		MiniPushObject.getInstance().setDeviceToken(regId);
		
		// 추가적인 device_token 과 사용자 정의된 id 매칭을 처리한다.
		
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub
		Log.i(tag, "onUnregistered -> registration : " + regId);
	}
	
	private void handleMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//
		String strJson = intent.getStringExtra("json_data");
		if ( strJson == null || strJson.length() == 0 ) {
			Log.i(tag, "null data received");
			return;
		}
		Log.i(tag, "receive -> " + strJson);
		
		//
		// json_data 를 사용자가 원하는 형식으로 변환한다.
		// 예) json_data -> {"alert":"제목입니다.", "user_data":"내용이 포함된 메시지입니다."}
		//
		String strAlert = null;
		String strUserData = null;
		try {
			JSONObject jo = new JSONObject(strJson);
			strAlert = jo.getString("alert").toString();
			strUserData = jo.getString("user_data").toString();
			
		} catch ( Exception e ) {
			e.printStackTrace();
			Log.i(tag, "alert or user_data not found in json_data");
			return;
		}

		// 사용자의 메시지 수신을 확인할 경우 사용하는 코드 
		long m_id = 0;
		String strMessageId = intent.getStringExtra("m_id");
		Log.i(tag, "receive -> " + strMessageId);
		try {
			m_id = Long.parseLong(strMessageId);			
		} catch ( Exception e ) { }
		Log.i(tag, "message id -> " + m_id);
		
		////
		MiniPushObject obj = MiniPushObject.getInstance();
		if ( obj.getContext() == null ) {
			obj.initilize(ctx, "515463805231");
		}
		/////////////////////////////////////
		// 수신된 push message 를 db 에 저장한다.
		PushMsgInfo info = new PushMsgInfo();
		//
		info.m_id = m_id;
		info.alert = strAlert;
		info.user_data = strUserData;
		//
		obj.putPushMessage(info);
		
		// 정상적으로 message 수신이 되었는지 확인한다.
		//MiniPushObject obj = MiniPushObject.getInstance();
		obj.verifyMessage(m_id);
		
		//
		mHandler.post(new DisplayToast(context, strAlert, strUserData, UserFunc.getNowTime()));
		
		//
		boolean bNotification = true;
		if ( bNotification )
			notificate(2, strAlert, strUserData);
	}
	
	//
	
	//
	private void notificate(int nCounter, String strTitle, String strUserData) {
		// notification 창에 보여지는 부분.
		int icon = R.drawable.ic_launcher; // icon from resources
		CharSequence tickerTtile = strTitle;
		CharSequence tickerText = strUserData;
		CharSequence contentText = strUserData;
		
		//
		Context context = getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		
		//
		JSONObject jo = new JSONObject();
		try {
			jo.put("alert", strTitle);
			jo.put("user_data", strUserData);
			
		} catch ( Exception e ) {
			
		}
		notificationIntent.putExtra("strParam", jo.toString());
		//
		
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, 
				notificationIntent, PendingIntent.FLAG_ONE_SHOT );
		//
		NotificationCompat.Builder notify = new NotificationCompat.Builder(context);
		notify.setTicker(tickerText);
		notify.setContentTitle(tickerTtile);
		notify.setContentText(contentText);
		//
		notify.setNumber(nCounter);
		int ledARGB = 0xff00ff00; // 0xARGB
		notify.setLights(ledARGB, 1000, 1000);
		//
		notify.setSmallIcon(icon);
		boolean bSound = false;
		if ( bSound )
			notify.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
		
		boolean bVibrate = false;
		if ( bVibrate )
			notify.setVibrate(new long[] {100L, 100L, 200L, 500L});
		
		// sound uri from resource
		//notify.setSound(Uri.parse("ddok"));
		
		notify.setAutoCancel(true);
		notify.setContentIntent(pendingIntent);
		//
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, tag);
		wl.acquire();
		//
		notificationManager.notify(PUSH_RECEIVER_NOTI, notify.getNotification());
		//
		boolean bShowActivity = true;
		if ( bShowActivity )
			pushAlert(context, strTitle, strUserData);
		//
		wl.release();
	}
	
	//
	private void pushAlert(Context context, String strTitle, String strMessage) {
		// 별도의 activity 를 보여주는 부분.
	}
	
	// For display Toast
	private class DisplayToast implements Runnable {
		Context mContext;
		String mAlert, mMessage, mTime;

		public DisplayToast(Context context, String title, String message, String strTime) {
			mContext = context;
			mAlert = title;
			mMessage = message;
			mTime = strTime;
		}

		public void run() {
			//
			if ( MainActivity.g_ctx != null ) {
				((MainActivity)MainActivity.g_ctx).setPushData(mAlert, mMessage, mTime);
			}
			
			//
			Toast tst = new Toast(mContext);
			tst = Toast.makeText(mContext, mAlert + "\n\n" + mMessage, Toast.LENGTH_LONG);
			tst.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			tst.show();
		}
	}
	
	////
}
