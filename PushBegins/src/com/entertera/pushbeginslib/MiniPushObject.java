package com.entertera.pushbeginslib;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class MiniPushObject {
	String tag = "MiniPushObject";
	String app_package_name;

	Context ctx = null;
	String strProjectNumber;
	String strDeviceToken;
	//
	//String strIdKind;
	String strDeviceId;
	String strServerAddress = null;

	//
	public String base_url = "http://www.pushbegins.com:18080/pbs_svc";
	public String web_url = base_url + "/Listener";
	public String master_url = base_url + "/Listener";

	// /////////////////////////////////////////////////////////////////////////////////////////
	// Singleton class
	private static MiniPushObject m_instance = new MiniPushObject();

	private MiniPushObject() {
		// change thread mode for use of network --> minSdkVersion="9"
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public static MiniPushObject getInstance() {
		return m_instance;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	//
	public void setContext(Context ctx) {
		this.ctx = ctx;
	}
	
	public Context getContext() {
		return this.ctx;
	}
	
	//
	public void initilize(Context ctx, String proj_num) {
		//
		if ( this.strProjectNumber != null && this.ctx != null )
			return;
		
		this.strProjectNumber = proj_num;
		this.ctx = ctx;
		//
		app_package_name = ctx.getPackageName();
		//
		if (!isNetworkAvalable()) {
			Log.e(tag, "internet connection invalid");
			return;
		}
		//
		registerGcmProcess(ctx);
		
		//
		UserDb db = UserDb.getInstance();
		db.setContext(ctx);
		
		// 분산서버를 사용하지 않을 경우 필요하지 않은 내용.
		strServerAddress = web_url;
		////
		if ( false ) {
		// 사용자별 서버의 주소를 얻어온다. push 보내는 서버의 주소를 돌려준다.
		// Local 에 이미 등록되어 있는 있는 서버인지 확인한다.
		UserHttpClient client = UserHttpClient.getInstance();
		//
		strServerAddress = db.getValue(DbDef.SERVER_ADDRESS);
		if ( strServerAddress != null && strServerAddress.length() > 7 ) {
			Log.i(tag, "Already registered address");
			client.setServerAddress(strServerAddress);
			
		} else {
			strServerAddress = client.getConnectAddress(this.strProjectNumber);
			if ( strServerAddress != null && strServerAddress.length() > 7 ) {
				if ( db.setKeyValue(DbDef.SERVER_ADDRESS, strServerAddress) ) {
					client.setServerAddress(strServerAddress);
					Log.i(tag, "First address");
				}
			}
		}
		}
		////

		// RegisterThread thread = new RegisterThread(ctx);
		// thread.start();
		
		//new AsyncRegister().execute(ctx);
	}

	//
	void registerGcmProcess(Context gcm_ctx) {
		// GCM part
		GCMRegistrar.checkDevice(gcm_ctx);
		GCMRegistrar.checkManifest(gcm_ctx);
		final String regId = GCMRegistrar.getRegistrationId(gcm_ctx);
		//
		if (regId == null || regId.length() == 0) {
			GCMRegistrar.register(gcm_ctx, strProjectNumber);
			//Log.i(tag, "Register GCM");
		} else {
			strDeviceToken = regId;
			//Log.i(tag, "Already registered GCM.\ndevice token -> " + strDeviceToken);
			return;
		}
		//
	}

	//
	public void setDeviceToken(String device_token) {
		//Log.i(tag, "setDeviceToken");
		this.strDeviceToken = device_token;
	}

	//
	public String getProjectNumber() {
		return strProjectNumber;
	}

	//
	class RegisterThread extends Thread {
		Context ctx = null;

		public RegisterThread(Context ctx) {
			this.ctx = ctx;
		}

		public void run() {
			registerGcmProcess(ctx);
		}
	}
	
	//// AsyncTask<초기값배열, 진행값배열, 결과값배열>
	class AsyncRegister extends AsyncTask<Context, Integer, Long> {

		Context mCtx = null;
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Long result) {
			Log.i(tag, "Thread END!!!");
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			Log.i(tag, "Thread START!!!!");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// setProgress(values[0]);
		}

		@Override
		protected Long doInBackground(Context... ctxParams) {
			long result = 0;
			//int numberOfParams = params.length;
			mCtx = ctxParams[0];
			registerGcmProcess(mCtx);
			//
			Log.i(tag, "AsyncRegister return");
			return result;
		}
	}

	// 등록된 모든 data 에 대해서 unique 한 값이면 된다.
	// id_kind + device_id 가 하나의 id 가 된다.
	// email, phone, cid 중 하나
	public boolean registerGcm(String device_id) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if (!isNetworkAvalable()) {
			Log.e(tag, "internet connection invalid");
			return false;
		}
		if (strDeviceToken == null || strDeviceToken.length() == 0) {
			Log.e(tag, 	"device token not set, wait then retry registerGcm(device_id)");
			return false;
		}
		//
		//this.strIdKind = id_kind;
		this.strDeviceId = device_id;

		// register to server
		GcmDeviceInfo info = new GcmDeviceInfo();
		//info.id_kind = strIdKind;
		info.device_id = strDeviceId;
		info.proj_num = strProjectNumber;
		info.device_token = strDeviceToken;
		//
		UserDb db = UserDb.getInstance();
		//
		UserHttpClient client = UserHttpClient.getInstance();
		int nDeviceSeq = client.registerDevice(info);
		if (nDeviceSeq > 0) {
			String strDeviceSeq = String.format("%d", nDeviceSeq);
			db.setKeyValue(DbDef.DEVICE_SEQ, strDeviceSeq);
			db.setKeyValue(DbDef.PROJ_NUM, strProjectNumber);
			//db.setKeyValue(DbDef.ID_KIND, strIdKind);
			db.setKeyValue(DbDef.DEVICE_ID, strDeviceId);

			return true;
		} else {
			return false;
		}
	}
	
	//
	public boolean verifyMessage(long m_id) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if (!isNetworkAvalable()) {
			Log.e(tag, "internet connection invalid");
			return false;
		}
		if ( m_id <= 0 ) {
			Log.e(tag,"message id invalid");
			return false;
		}
		UserDb db = UserDb.getInstance();
		if ( db.hasMessageId(m_id) ) {
			Log.e(tag, "already has message id");
			return false;
		}
	
		// verify message id to server
		UserHttpClient client = UserHttpClient.getInstance();
		if ( !client.verifyMessageId(m_id) ) {
			Log.e(tag, "verify message id from server failure");
			return false;
		}
		
		if ( !db.putMessageId(m_id) ) {
			Log.e(tag, "record message id faliure");
			return false;
		}
		
		return true;
	}
	
	//
//	// device_seq = MiniPushObject.getInstance().getValue(DbDef.DEVICE_SEQ)
//	public boolean confirmMessage(long message_id, int device_seq) {
	public boolean confirmMessage(long m_id) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if (!isNetworkAvalable()) {
			Log.e(tag, "internet connection invalid");
			return false;
		}
		if ( m_id <= 0 ) {
			Log.e(tag,"message id invalid");
			return false;
		}
		UserDb db = UserDb.getInstance();
		String strSeq = db.getValue(DbDef.DEVICE_SEQ);
		int nSeq = 0;
		try {
			nSeq = Integer.parseInt(strSeq);
		} catch ( Exception e ) {
			//nSeq = 0;
			Log.e(tag,"device_seq invalid");
			return false;
		}
	
		// verify message id to server
		UserHttpClient client = UserHttpClient.getInstance();
		if ( !client.confirmMessage(m_id, nSeq) ) {
			Log.e(tag, "contirm message from server failure");
			return false;
		}
		
		if ( !db.confirmMessage(m_id) ) {
			Log.e(tag, "contirm message faliure");
			return false;
		}
		
		return true;
	}
	//
	public boolean sendCusomerMessage(String strMessage) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if (!isNetworkAvalable()) {
			Log.e(tag, "internet connection invalid");
			return false;
		}
		if ( strMessage == null || strMessage.length() == 0 ) {
			Log.e(tag,"message invalid");
			return false;
		}
	
		// send customer message to server
		int nDeviceSeq = 0;
		UserDb db = UserDb.getInstance();
		try {
			nDeviceSeq = Integer.parseInt(db.getValue(DbDef.DEVICE_SEQ));	
		} catch ( Exception e ) {
			//
		}
		if ( nDeviceSeq == 0 ) return false;
		
		String strProjectNumber = db.getValue(DbDef.PROJ_NUM);
		if ( strProjectNumber == null || strProjectNumber.length() == 0 ) return false;
		
		UserHttpClient client = UserHttpClient.getInstance();
		if ( !client.sendCustomerMessage(nDeviceSeq, strProjectNumber, strMessage) ) {
			Log.e(tag, "send customer message failure");
			return false;
		}
		
		return true;
	}
	
	//////////
	public boolean putPushMessage(PushMsgInfo info) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if ( info.m_id <= 0 ) {
			Log.e(tag,"message id invalid");
			return false;
		}
		UserDb db = UserDb.getInstance();
		if ( !db.putPushMessage(info.m_id, info.alert, info.user_data) ) {
			Log.e(tag, "record push message faliure");
			return false;
		}
		
		return true;
	}
	
	//
	public boolean deletePushMessage(long m_id) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		if ( m_id <= 0 ) {
			Log.e(tag,"message id invalid");
			return false;
		}
		UserDb db = UserDb.getInstance();
		if ( !db.deletePushMessage(m_id) ) {
			Log.e(tag, "delete push message faliure");
			return false;
		}
		
		return true;
	}
	
	//
	public boolean deleteAllPushMessages() {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return false;
		}
		UserDb db = UserDb.getInstance();
		if ( !db.deleteAllPushMessages() ) {
			Log.e(tag, "delete all push messages faliure");
			return false;
		}
		
		return true;
	}
	
	//
	public List<PushMsgInfo> getAllPushMessageLists() {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return null;
		}
		UserDb db = UserDb.getInstance();
		List<PushMsgInfo> lResult = db.getAllPushMessageLists();
		return lResult;
	}
	
	//
	public List<PushMsgInfo> getPushMessageList(int nLimit, int nOffset) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return null;
		}
		UserDb db = UserDb.getInstance();
		List<PushMsgInfo> lResult = db.getPushMessageList(nLimit, nOffset);
		return lResult;
	}
	
	//
	public PushMsgInfo getLastPushMessage() {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return null;
		}
		UserDb db = UserDb.getInstance();
		List<PushMsgInfo> lResult = db.getPushMessageList(1, 0);
		if ( lResult == null || lResult.size() == 0 ) {
			return null;
		}
		//
		PushMsgInfo info = lResult.get(0);
		return info;
	}
	
	//
	public PushMsgInfo getPushMessage(long m_id) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return null;
		}
		
		UserDb db = UserDb.getInstance();
		return db.getPushMessage(m_id);
	}
	
	public List<PushMsgInfo> getPushMessageList_100(int nPage) {
		if (ctx == null) {
			Log.e(tag, "initilizeObject needed");
			return null;
		}
		//
		UserDb db = UserDb.getInstance();
		//
		int page_size = 100;
		int start_pos = nPage * page_size;
		//
		List<PushMsgInfo> ltResult = db.getPushMessageList(page_size, start_pos);
		return ltResult;
	}

	//////////
	public boolean isNetworkAvalable() {

		if (!MyNetworkInfo.IsWifiAvailable(ctx) && !MyNetworkInfo.Is3GAvailable(ctx)) {
			// Toast.makeText(this, "네크워크에 연결할 수 없습니다.",
			// Toast.LENGTH_SHORT).show();
			return false;

		} else {
			return true;
		}
	}
	
	//
	public String getPhoneNumber() {
		String strPhoneNumber = PhoneInfo.getPhoneNumber(ctx);
		if ( strPhoneNumber != null ) {
			String strISONumber = "+" + PhoneInfo.getISONumber(ctx);
			String strHeaderNumber = strPhoneNumber.substring(0, 3);
			// +82 로 시작하는 경우
			if ( strHeaderNumber.equals(strISONumber) ) {
				strPhoneNumber = strPhoneNumber.replace(strISONumber, "0");	
			}
		}
		return strPhoneNumber;
	}
	
	// MiniPushObject database function
	public boolean setKeyValue(String strKey, String strValue) {
		UserDb db = UserDb.getInstance();
		return db.setKeyValue(strKey, strValue);
	}
	
	public String getValue(String strKey) {
		UserDb db = UserDb.getInstance();
		return db.getValue(strKey);
	}
	
	public void deleteKey(String strKey) {
		UserDb db = UserDb.getInstance();
		db.deleteKey(strKey);
	}
	
	//
	public long getLastMessageId() {
		UserDb db = UserDb.getInstance();
		return db.getLastMessageId();
	}
	
	////
}
