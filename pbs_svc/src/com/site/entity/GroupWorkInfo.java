package com.site.entity;

import java.util.ArrayList;
import java.util.List;

public class GroupWorkInfo {
	public String push_name, group_id;
	public int push_user_seq, group_seq;
	
//	// json array
//	public String device_seq_list;
//	public String device_token_list;
//	public String device_id_list;
	//
	public List<Integer> device_seq_list;
	//
	public GroupWorkInfo() {
		device_seq_list = new ArrayList<Integer>();
	}
}
