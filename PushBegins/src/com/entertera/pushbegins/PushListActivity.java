package com.entertera.pushbegins;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.entertera.pushbeginslib.DbDef;
import com.entertera.pushbeginslib.MiniPushObject;
import com.entertera.pushbeginslib.PushMsgInfo;

public class PushListActivity extends Activity {

	//
	static int DETAIL_INFO = 3;
	//
	Context ctx;
	//
	ArrayList<PushMsgInfo> arItem;
	MyListAdapter myAdapter;	
	ListView pushList;
	//
	MiniPushObject obj = MiniPushObject.getInstance();
	//
	String tag = "main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_list);
		//
		ctx = this;
		//
		//
		arItem = new ArrayList<PushMsgInfo>();
		myAdapter = new MyListAdapter(this, arItem);

		pushList = (ListView)findViewById(R.id.listMsg);
		pushList.setAdapter(myAdapter);
		
		//
		getAllPushMessages();
		
		//
		// Button click event
		//
		OnBtnClickListener btnListener = new OnBtnClickListener();
		//
		ImageButton btnBack = (ImageButton)findViewById(R.id.btnBack);
		ImageButton btnDeleteAll = (ImageButton)findViewById(R.id.btnDeleteAll);
		//
		btnBack.setOnClickListener(btnListener);
		btnDeleteAll.setOnClickListener(btnListener);
	}
	
	//
	class OnBtnClickListener implements OnClickListener {
		//
		public void onClick(final View view) {

			int nCase = view.getId();
			if ( nCase == R.id.btnBack ) {
				Log.i(tag, "back button click...");
				finish();
				
			} else if ( nCase == R.id.btnDeleteAll ) {
				acceptDeleteAllDialog();
				
			} else {
				//
			}
		}
	}
	
	//
	//
	// Image 선택 dialog
	//
	public void acceptDeleteAllDialog() {
		AlertDialog.Builder mAlert = new AlertDialog.Builder(ctx);
		mAlert.setCancelable(true);

		mAlert.setTitle("모든 Push Message 삭제");
		mAlert.setMessage("모든 Push Message를 삭제하시겠습니까? 삭제된 message 는 복원할 수 없습니다.");
		mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				//
				obj.deleteAllPushMessages();
				getAllPushMessages();
				//
			}
		});
		mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				//
			}
		});

		mAlert.show();
	}
	
	////
	private static class ViewHolder {
		//
		LinearLayout loMsg;
		//
		ImageView imgProfile;
		//
		TextView txtAlert;
		TextView txtUserData;
		TextView txtTime;
		//
		ImageButton btnDeleteMsg;
	}
		
	////
	
	////////////////////////////////////////////////////////////////////////////
	// adapter
	class MyListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ArrayList<PushMsgInfo> arSrc;
		//
		public MyListAdapter(Context context, ArrayList<PushMsgInfo> arItem) {
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = arItem;
		}

		public int getCount() {
			return arSrc.size();
		}

		public PushMsgInfo getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			//
			View view = convertView;
			final ViewHolder holder;
			int layout = R.layout.m_push_item_1;
			if ( convertView == null ) {
				view = mInflater.inflate(layout, parent, false);
				holder = new ViewHolder();
				//
				holder.loMsg = (LinearLayout)view.findViewById(R.id.layoutPushMsg);
				//
				holder.imgProfile = (ImageView)view.findViewById(R.id.imgProfile);
				//
				holder.txtTime = (TextView)view.findViewById(R.id.txtTime);
				holder.txtAlert = (TextView)view.findViewById(R.id.txtTitle);
				holder.txtUserData = (TextView)view.findViewById(R.id.txtMessage);
				//
				view.setTag(holder);
			} else {
				holder = (ViewHolder)view.getTag();
			}

			//
			PushMsgInfo data = arSrc.get(position);
			if ( data == null ) {
				return convertView;
			}
			
			//
			holder.txtTime.setText(data.msg_time);
			holder.txtAlert.setText(data.alert);
			holder.txtUserData.setText(data.user_data);

			//
			// 행 전체에 click 이벤트 처리가 필요.
			//
			OnListViewBtnClickListener btnListener = new OnListViewBtnClickListener();
			//
			holder.loMsg.setTag((Integer)position);
			holder.loMsg.setOnClickListener(btnListener);
			//
			//holder.btnDeleteLetter.setTag((Integer)position);
			//holder.btnDeleteLetter.setOnClickListener(btnListener);

			return view;
		}

		//

		////////////////////////////////////////////////////////////////////////
		// ImageButton (within listview) click event process
		class OnListViewBtnClickListener implements OnClickListener {
			public void onClick(View view) {
				int nCase = view.getId();
				int nPosition = (Integer)view.getTag();
				if ( nCase == R.id.layoutPushMsg ) {
					//
					PushMsgInfo data = arItem.get(nPosition);
					// 
					//
					long nParam = data.m_id;
					Intent intent = new Intent(ctx, PushDetailActivity.class);
					intent.putExtra("userParam", nParam);
					//startActivity(intent);
					startActivityForResult(intent, DETAIL_INFO);
					//

				} else {

				}
				//
			}
		}
		////////////////////////////////////////////////////////////////////////
	}
	
	//
	void getAllPushMessages() {
		List<PushMsgInfo> lResult = obj.getAllPushMessageLists();
		if ( lResult == null || lResult.size() == 0 ) {
			Log.i(tag, "no record");
			arItem.clear();
			myAdapter.notifyDataSetChanged();
			return;
		}

		//
		arItem.clear();
		int nItems = lResult.size();
		for ( int i = 0; i < nItems; i++ ) {
			PushMsgInfo info = (PushMsgInfo)lResult.get(i);
    		arItem.add(info);
    	}
		//
		myAdapter.notifyDataSetChanged();
		pushList.setSelection(0);
	}
	
	////
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//strDebug = String.format("requestCode: %d", requestCode);
		//Log.i(tag, strDebug);

		////////
		if ( requestCode == DETAIL_INFO ) {
			getAllPushMessages();
			
		} else {

		}
		////////
	}
	
	//////////
}
