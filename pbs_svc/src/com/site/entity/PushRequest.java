package com.site.entity;

import java.util.ArrayList;
import java.util.List;

public class PushRequest {
	public String user_id, push_name, alert, user_data;
	public String badge, sound; // for APNS
	//public List<String> ext_id_list;
	public List<String> token_list;
	//
	public PushRequest() {
		token_list = new ArrayList<String>();
		badge = "1";
		sound = "default";
	}
}
