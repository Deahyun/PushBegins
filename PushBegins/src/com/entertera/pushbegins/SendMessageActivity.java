package com.entertera.pushbegins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.entertera.pushbeginslib.DbDef;
import com.entertera.pushbeginslib.MiniPushObject;

public class SendMessageActivity extends Activity {
	Context ctx;
	//
	EditText m_Edit;
	//
	MiniPushObject obj = MiniPushObject.getInstance();
	//
	String tag = "send_message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);
		//
		ctx = this;
		//
		m_Edit = (EditText)findViewById(R.id.editText1);
		//
		// Button click event
		//
		OnBtnClickListener btnListener = new OnBtnClickListener();
		//
		ImageButton btnBack = (ImageButton)findViewById(R.id.btnBack);
		Button btnSend = (Button)findViewById(R.id.buttonYes);
		Button btnCancel = (Button)findViewById(R.id.buttonNo);
		//
		btnBack.setOnClickListener(btnListener);
		btnSend.setOnClickListener(btnListener);
		btnCancel.setOnClickListener(btnListener);
		
		// For debug
		if ( false ) {
			String strSeq = MiniPushObject.getInstance().getValue(DbDef.DEVICE_SEQ);
			Log.i(tag,  "device_seq -> " + strSeq);
		}
	}
	
	//
	class OnBtnClickListener implements OnClickListener {
		//
		public void onClick(final View view) {

			int nCase = view.getId();
			if ( nCase == R.id.btnBack ) {
				finish();
				
			} else if ( nCase == R.id.buttonYes ) {
				String strMessage = m_Edit.getText().toString();
				if ( strMessage == null || strMessage.length() == 0 ) {
					Toast.makeText(ctx, "입력한 메시지가 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if ( !obj.sendCusomerMessage(strMessage) ) {
					Toast.makeText(ctx, "메시지 보내기가 실패했습니다.", Toast.LENGTH_SHORT).show();
					Log.i(tag, "sendCusomerMessage failure");
					return;
					
				} else {
					Toast.makeText(ctx, "서비스 제공자에게 메시지를 보냈습니다.", Toast.LENGTH_SHORT).show();
				}
				finish();
				
			} else if ( nCase == R.id.buttonNo ) {
				finish();
				
			} else {
				//
			}
		}
	}
	////
}
