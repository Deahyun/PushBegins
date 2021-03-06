package com.entertera.pushbegins;

import com.entertera.pushbeginslib.MiniPushObject;
import com.entertera.pushbeginslib.PushMsgInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PushDetailActivity extends Activity {
	Context ctx;
	
	PushMsgInfo mInfo;
	MiniPushObject obj = MiniPushObject.getInstance();
	String tag = "detail";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_detail);
		//
		ctx = this;
		//
		Intent intent = getIntent();
		long nParam = intent.getLongExtra("userParam", 0);
		if ( nParam <= 0 ) {
			return;
		}
		//
		mInfo = obj.getPushMessage(nParam);
		//
		TextView msgTitleView = (TextView)findViewById(R.id.msgTitle);
		TextView regTmView = (TextView)findViewById(R.id.reg_tm);
		TextView contentView = (TextView)findViewById(R.id.content);
		//
		msgTitleView.setText(mInfo.alert);
		regTmView.setText(mInfo.msg_time);
		contentView.setText(Html.fromHtml(mInfo.user_data));
		
		//
		//
		// Button click event
		//
		OnBtnClickListener btnListener = new OnBtnClickListener();
		//
		ImageButton btnBack = (ImageButton)findViewById(R.id.btnBack);
		Button btnDelete = (Button)findViewById(R.id.btnDelete);
		//
		btnBack.setOnClickListener(btnListener);
		btnDelete.setOnClickListener(btnListener);
	}
	
	//
	class OnBtnClickListener implements OnClickListener {
		//
		public void onClick(final View view) {

			int nCase = view.getId();
			if ( nCase == R.id.btnBack ) {
				Log.i(tag, "back button click...");
				finish();
				
			} else if ( nCase == R.id.btnDelete ) {
				if ( obj.deletePushMessage(mInfo.m_id) ) {
					Toast.makeText(ctx, "Push 메시지 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
					finish();
					
				} else {
					Toast.makeText(ctx, "Push 메시지 삭제를 실패했습니다.", Toast.LENGTH_SHORT).show();
				}
				
			} else {
				//
			}
		}
	}
	//////////
}
