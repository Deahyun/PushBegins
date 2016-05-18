package com.site.entity;

public class GcmDeviceInfo {
	public String device_token, proj_num;
	// device_id -> "a/i" + "|" + "proj_num/app_id" + "|" +"email, phone, cid 중 하나"
	public String device_id;
	public int seq;
}
