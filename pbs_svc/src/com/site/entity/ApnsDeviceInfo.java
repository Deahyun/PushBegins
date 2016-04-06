package com.site.entity;

public class ApnsDeviceInfo {
	public String device_token, app_id;
	// device_id -> email, phone, cid 중 하나
	public String id_kind, device_id;
	public int seq;
}
