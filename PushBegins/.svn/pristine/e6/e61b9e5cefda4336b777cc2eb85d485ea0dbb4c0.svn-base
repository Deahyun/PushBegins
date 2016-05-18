package com.entertera.pushbegins;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

//
//Show progress dialog class
class WaitDlg extends Thread {
	Context mContext;
	String mTitle;
	String mMsg;
	ProgressDialog mProgress;
	Looper mLoop;

	WaitDlg(Context context, String title, String msg) {
		mContext = context;
		mTitle = title;
		mMsg = msg;

		setDaemon(true);
	}

	public void run() {
		try {
			Looper.prepare();
			mProgress = new ProgressDialog(mContext);
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setTitle(mTitle);
			mProgress.setMessage(mMsg);
			mProgress.setCancelable(false);
			mProgress.show();
	
			mLoop = Looper.myLooper();
			Looper.loop();
			
		} catch ( Exception e ) {
			//
			e.printStackTrace();
		}
	}

	public static void stop(WaitDlg dlg) {
		if ( dlg != null ) {
			dlg.mProgress.dismiss();
	
			// Until dismiss dialog wiat...
			try { Thread.sleep(300); } catch (InterruptedException e) { ; }
			try {
				dlg.mLoop.quit();
				
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}