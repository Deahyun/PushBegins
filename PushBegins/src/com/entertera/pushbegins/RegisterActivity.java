package com.entertera.pushbegins;

import com.entertera.pushbeginslib.DbDef;
import com.entertera.pushbeginslib.MiniPushObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	Context ctx;
	Bundle extra;
	Intent resIntent;
	//
	EditText mEdit;
	//
	RadioButton mRadioEmail, mRadioPhone, mRadioCID;
	String strIdKind = null; // email:'e' / phone:'p' / cid:'c'
	//
	MiniPushObject obj = MiniPushObject.getInstance();
	//
	String tag = "register";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		ctx = this;
		extra = new Bundle();
		resIntent = getIntent();
		//
		mEdit = (EditText)findViewById(R.id.editText1);
		//
		OnBtnClickListener btnListener = new OnBtnClickListener();
		//
		Button btnYes = (Button)findViewById(R.id.buttonYes);
		Button btnNo = (Button)findViewById(R.id.buttonNo);
		//
		btnYes.setOnClickListener(btnListener);
		btnNo.setOnClickListener(btnListener);
		
		//
		mRadioEmail = (RadioButton)findViewById(R.id.radio_email);
		mRadioPhone = (RadioButton)findViewById(R.id.radio_phone);
		mRadioCID = (RadioButton)findViewById(R.id.radio_cid);
		//
		mRadioEmail.setOnClickListener(btnListener);
		mRadioPhone.setOnClickListener(btnListener);
		mRadioCID.setOnClickListener(btnListener);
		
		// 기본은 email 선택
		mRadioEmail.setChecked(true);
		strIdKind = "e";
		
		//
		if ( false ) {
			String strPhoneNumber = obj.getPhoneNumber();
			if ( strPhoneNumber == null || strPhoneNumber.length() == 0 ) {
				strPhoneNumber = "01099999999";
			}
			mEdit.setText(strPhoneNumber);
			//
			mEdit.setSelection(mEdit.length());
			//
			//mEdit.requestFocus();
		}
	}
	
	//
	public void activityReturn(int nResult) {
		
		if ( nResult > 1 ) {
			this.setResult(RESULT_CANCELED, resIntent);
			this.finish();
			return;
		}
		extra.putInt("intParam", nResult);
		resIntent.putExtras(extra);
		//
		this.setResult(RESULT_OK, resIntent);
		this.finish();
	}
	//
	
	//
	class OnBtnClickListener implements OnClickListener {
		//
		//
		public void onClick(final View view) {

			//
			int nCase = view.getId();
			if ( nCase == R.id.buttonYes ) {
				//
				boolean bAvailableNetwork = obj.isNetworkAvalable();
				if ( !bAvailableNetwork ) {
					Toast.makeText(ctx, "인터넷에 연결할 수 없습니다. 인터넷 연결을 확인후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
					return;
				}
				//
				//
				String strDeviceId = mEdit.getText().toString();
				if ( strDeviceId.length() < 4 ) {
					Toast.makeText(ctx, "아이디는 4자 보다 길어야 합니다. ", Toast.LENGTH_SHORT).show();
					return;
				}

				if ( false ) {
					// 분산 서버까지 고려할 경우 필요한 내용
					String svr = obj.getValue(DbDef.SERVER_ADDRESS);
					Log.i(tag, svr);
				}

				WaitDlg dlg = new WaitDlg(ctx, "Network progress", "Processing...");
				dlg.start();
				if ( obj.registerGcm(strDeviceId) ) {
					// 등록 성공
					Log.i(tag, "gcm 등록 성공");
					Toast.makeText(ctx, "gcm 등록 성공", Toast.LENGTH_SHORT).show();
				} else {
					// 등록 실패
					Log.i(tag, "gcm 등록 실패");
					Toast.makeText(ctx, "초기화 오류입니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
					WaitDlg.stop(dlg);
					return;
				}				//
				WaitDlg.stop(dlg);
				
				// 등록성공후 처리
				//obj.setKeyValue("device_id", strDeviceId);
				activityReturn(1);
				
			} else if ( nCase == R.id.buttonNo ) {
				activityReturn(0);
				
			} else if ( nCase == R.id.radio_email ) {
				setRadio(0);
				
			} else if ( nCase == R.id.radio_phone ) {
				setRadio(1);
				
			} else if ( nCase == R.id.radio_cid ) {
				setRadio(2);
				
			} else {

			}
		}
		//
	}
	
	// nCase: 0 ~ 2
	void setRadio(int nCase) {
		if ( nCase == 0 ) {
			// email
			strIdKind = "e";
			mRadioEmail.setChecked(true);
			mRadioPhone.setChecked(false);
			mRadioCID.setChecked(false);
			
		} else if ( nCase == 1 ) {
			// phone
			strIdKind = "p";
			mRadioEmail.setChecked(false);
			mRadioPhone.setChecked(true);
			mRadioCID.setChecked(false);
			
		} else if ( nCase == 2 ) {
			// cid
			strIdKind = "c";
			mRadioEmail.setChecked(false);
			mRadioPhone.setChecked(false);
			mRadioCID.setChecked(true);
			
		} else {
			// n/a
		}
	}
	
	////
}
