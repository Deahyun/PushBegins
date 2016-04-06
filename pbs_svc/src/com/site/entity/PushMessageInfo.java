package com.site.entity;

public class PushMessageInfo {
	public long m_id;
	public int seq, total_cnt, send_cnt, success_cnt;
	public int group_seq; // 전체 발송시 0; 실제 그룹 사용시는 해당 group_seq
	public int user_seq; // 개별 사용자에게 발송시 해당 사용자의 user_seq
	public String message_type, alert, user_data;
	public String push_name;
	public String reg_tm;
}
