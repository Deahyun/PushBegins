package com.entertera.pushbegins;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
	String tag = "splash";
	String strDebug;
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		//
		int nTimeout = 500;
		Handler h = new Handler();
		h.postDelayed(new splashHandler(), nTimeout);
	}
	
	//
	class splashHandler implements Runnable {
		public void run() {
			startActivity( new Intent(getApplication(), MainActivity.class) );
			//
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();		
		}
	}
	////
}
