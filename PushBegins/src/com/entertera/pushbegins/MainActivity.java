package com.entertera.pushbegins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.entertera.pushbeginslib.DbDef;
import com.entertera.pushbeginslib.MiniPushObject;
import com.entertera.pushbeginslib.PushMsgInfo;

public class MainActivity extends Activity {

	// Project ID: push-begins
	// Project Number: 515463805231
	// API KEY: AIzaSyDLOjbrjAVQv3ewnblr3f0-7EnnxB14maw

	static int REGISTER_INFO = 1;
	static int CHANGE_INFO = 2;
	//
	public static Context g_ctx;
	//
	TextView txtID, txtMessage, txtTime;
	//
	String tag = "main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		//
		g_ctx = this;
		//
		MiniPushObject obj = MiniPushObject.getInstance();
		// public void initilizeObject(Context ctx, String proj_num)
		// initilizeObject(...) 이 완료되면 GCMIntentService 의 onRegistered(...) 이 실행된다.
		//   대략 수초의 시간이 소요된다.
		obj.initilize(this, "515463805231");
		
		//
		loadMainView();
		
//		// 버튼 등록 코드
//		OnBtnClickListener btnListener = new OnBtnClickListener();
//		//
//		Button btnRegister = (Button)findViewById(R.id.btnRegister);
//		//
//		btnRegister.setOnClickListener(btnListener);
//		
//		// TextView 에 관한 코드
//		tv1 = (TextView)findViewById(R.id.textView1);
//		tv2 = (TextView)findViewById(R.id.textView2);
//		
//		//
//		Intent intent = getIntent();
//		String strParam = intent.getStringExtra("strParam");
//		if ( strParam == null || strParam.length() == 0 ) {
//			// 사용자가 실행시킬 경우
//			// 별도의 저장소에 push 의 내용을 저장시킨후 불러오는 과정이 필요.
//			
//		} else {
//			// notification 에서 실행된 경우
//			// GCMIntentService 에서 "strParam" 의 변수에 json 형식의 data 전달됨.
//			Log.i(tag, strParam);
//			String strAlert = null;
//			String strUserData = null;
//			try {
//				JSONObject jo = new JSONObject(strParam);
//				strAlert = jo.getString("alert").toString();
//				strUserData = jo.getString("user_data").toString();
//				//
//				setPushData(strAlert, strUserData);
//				
//			} catch ( Exception e ) {
//				e.printStackTrace();
//			}
//		}
	}
	
	class OnBtnClickListener implements OnClickListener {
		//
		//
		public void onClick(final View view) {

			//
			int nCase = view.getId();
			if ( nCase == R.id.buttonChangeInfo ) {
				Intent intent = new Intent(g_ctx, ChangeInfoActivity.class);
				startActivityForResult(intent, CHANGE_INFO);
				
			} else if ( nCase == R.id.buttonPushList ) {
				Intent intent = new Intent(g_ctx, PushListActivity.class);
				startActivity(intent);
				
			} else if ( nCase == R.id.buttonConfirm ) {
				MiniPushObject obj = MiniPushObject.getInstance();
				long message_id = obj.getLastMessageId();
				obj.confirmMessage(message_id);
				
			} else if ( nCase == R.id.buttonSendMessage ) {
				Intent intent = new Intent(g_ctx, SendMessageActivity.class);
				startActivity(intent);
				
			} else {

			}
		}
		//
	}
	
	// GCMIntentService 에서 호출되어서 Activity 의 data 를 변경시킨다.
	public void setPushData(String strAlert, String strUserData, String strTime) {
		String strMessage = String.format("[%s]\n\n%s", strAlert, strUserData);
		txtMessage.setText(strMessage);
		//
		txtTime.setText(strTime);
	}
	
	//
	void loadMainView() {
		MiniPushObject obj = MiniPushObject.getInstance();
		String strDeviceId = obj.getValue(DbDef.DEVICE_ID);
		if ( strDeviceId == null || strDeviceId.length() < 4 ) {
			Intent intent = new Intent(g_ctx, RegisterActivity.class);
			startActivityForResult(intent, REGISTER_INFO);
			return;
		}
		
		// 정상적으로 gcm 등록후 실행되는 부분
		txtID = (TextView)findViewById(R.id.textViewID);
		txtMessage = (TextView)findViewById(R.id.textViewMessage);
		txtTime = (TextView)findViewById(R.id.textViewTime);
		
		//
		txtID.setText("ID: " + strDeviceId);
		
		//
		PushMsgInfo info = obj.getLastPushMessage();
		if ( info != null ) {
			setPushData(info.alert, info.user_data, info.msg_time);
		}
		
		// 버튼 등록 코드
		OnBtnClickListener btnListener = new OnBtnClickListener();
		//
		Button btnChangeInfo = (Button)findViewById(R.id.buttonChangeInfo);
		Button btnPushList = (Button)findViewById(R.id.buttonPushList);
		Button btnConfirm = (Button)findViewById(R.id.buttonConfirm);
		Button btnSendMessage = (Button)findViewById(R.id.buttonSendMessage);
		//
		btnChangeInfo.setOnClickListener(btnListener);
		btnPushList.setOnClickListener(btnListener);
		btnConfirm.setOnClickListener(btnListener);
		btnSendMessage.setOnClickListener(btnListener);
	}
	
	////
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//strDebug = String.format("requestCode: %d", requestCode);
		//Log.i(tag, strDebug);

		////////
		if ( requestCode == REGISTER_INFO ) {
			if ( resultCode != RESULT_OK ) {
				Log.i(tag, "onActivityResult failure: resultCode");
				Toast.makeText(this, "기본정보가 등록되지 않으면 앱을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			//
			int nRes = data.getIntExtra("intParam", 0);
			if ( nRes != 1 ) {
				Log.i(tag, "onActivityResult failure: param");
				Toast.makeText(this, "기초정보를 등록하지 않으면 이 앱을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			//
			loadMainView();
			
		} else if ( requestCode == CHANGE_INFO ) {
			if ( resultCode != RESULT_OK ) {
				Log.i(tag, "onActivityResult failure: resultCode");
				Toast.makeText(this, "ID 변경 취소 또는 실패 입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			//
			MiniPushObject obj = MiniPushObject.getInstance();
			String strID = String.format("ID: %s", obj.getValue(DbDef.DEVICE_ID));
			txtID.setText(strID);
			
		} else {

		}
		////////
	}
	
	////
}
