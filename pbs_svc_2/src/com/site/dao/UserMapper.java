package com.site.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.site.entity.ApnsDeviceInfo;
import com.site.entity.CommonDeviceInfo;
import com.site.entity.CustomerMessageInfo;
import com.site.entity.DeviceIdInfo;
import com.site.entity.DeviceMapInfo;
import com.site.entity.DeviceTokenExInfo;
import com.site.entity.GcmDeviceInfo;
import com.site.entity.GroupWorkInfo;
import com.site.entity.KeyTokenInfo;
import com.site.entity.PushMessageInfo;
import com.site.entity.PushUserInfo;
import com.site.entity.RegisterApnsInfo;
import com.site.entity.RegisterGcmInfo;

public interface UserMapper {
//	// samples
//	public User selectUser(String id);
//	public List<User> selectAllUsers();
//	public void insertUser(User user);
//	public void updateUser(@Param("id") String id, @Param("info") String info);
//	public void deleteUser(String id);
//	//
//	public int getUserCount();
//	
//	// magzine service
//	public int getDeviceTokenCount(String uuid);
//	public boolean insertDeviceToken(@Param("uuid") String uuid, @Param("device_token") String device_token);
//	public boolean updateDeviceToken(@Param("uuid") String uuid, @Param("device_token") String device_token);
//	//
//	public List<DeviceInfo> selectAllDevices();
	
	// pushbegins service
	public int getGcmDeviceSeq(@Param("device_token") String strDeviceToken);
	public int getGcmDeviceSeq2(@Param("device_id") String strDeviceId);
	public void deleteGcmDevice(@Param("device_token") String strDeviceToken);
	public void insertGcmDevice(GcmDeviceInfo info);
	public void updateGcmDevice(GcmDeviceInfo info);
	
	// Device map
	//public DeviceMapInfo getGcmAppInfo(@Param("proj_num") String strProjNum);
	//public DeviceMapInfo getApnsAppInfo(@Param("app_id") String strAppId);
	public int hasDeviceMap(DeviceMapInfo info);
	
	public int getPushUserSeq(String email);
	
	public List<String> getAllGcmDeviceTokenList(String strProjNum);
	public List<String> getGcmGroupDeviceTokenList(@Param("device_seq_list") List<Integer> device_seq_list);
	//
	public List<GcmDeviceInfo> getGcmDeviceList(String strProjNum);
	
	// 공용서비스
	public DeviceTokenExInfo getDeviceTokenEx(@Param("device_id") String strDeviceId);
	
	
	// apns
	public int getApnsDeviceSeq(@Param("device_token") String strDeviceToken);
	public int getApnsDeviceSeq2(@Param("device_id") String strDeviceId);
	public void deleteApnsDevice(@Param("device_token") String strDeviceToken);
	public void insertApnsDevice(ApnsDeviceInfo info);
	public void updateApnsDevice(ApnsDeviceInfo info);
	
	//////////
	
	// pushbegins service
	public int getUserCount(String email);
	public int getUserSeq(String email);
	public void deleteGcmDeviceToken(String device_token);
	public void insertUser(GcmDeviceInfo info);
	public void updateUser(GcmDeviceInfo info);
	public void activateGcmUser(String email);
	public void deleteGcmUser(String email);
	public void loginGcmUser(String email);
	public KeyTokenInfo getGcmKeyToken(@Param("email") String UserId, @Param("proj_num") String ProjNum);
	
	//
	public void insertPushUser(PushUserInfo info);
	public int authPushUser(@Param("email") String UserId, @Param("pwd") String Pwd);
	//public int getPushUserSeq(String email);
	//
	//public void registerGcmPush(RegisterGcmInfo info);
	public void registerGcmPush(@Param("email") String strEmail, @Param("push_name") String strPushName, @Param("proj_num") String strProjNum, @Param("api_key") String strApiKey);
	
	//
	public String getApiKey(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
	public String getProjNum(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
	//
	//public List<String> getAllGcmDeviceTokenList(String strProjNum);
	//public List<String> getGroupGcmDeviceTokenList(@Param("proj_num")String strProjNum, @Param("list") List<String> lParam);
	//
	//public List<GcmDeviceInfo> getGcmUserList(String strProjNum);
	//
	public List<RegisterGcmInfo> getGcmPushList(int nUserSeq);
	public List<RegisterApnsInfo> getApnsPushList(int nUserSeq);
	//
	public List<PushMessageInfo> getPushMessageList(@Param("user_seq") int nUserSeq, @Param("start_pos") int nStartPos, @Param("page_size") int nPageSize);
	public List<CustomerMessageInfo> getCustomerMessageList(@Param("user_seq") int nUserSeq, @Param("start_pos") int nStartPos, @Param("page_size") int nPageSize);
	public String getDeviceId(@Param("device_seq") int nUserSeq);
	
	//
	public List<String> getAppList(@Param("user_seq") int nUserSeq, @Param("start_pos") int nStartPos, @Param("page_size") int nPageSize);
	public String getGcmProjNum(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
	public String getApnsAppId(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
	public List<String> getPushDeviceList(@Param("proj_num") String strProjNum, @Param("app_id") String strAppId, @Param("start_pos") int nStartPos, @Param("page_size") int nPageSize);
	//
	public List<DeviceIdInfo> getPushDeviceListEx(@Param("proj_num") String strProjNum, @Param("app_id") String strAppId, @Param("start_pos") int nStartPos, @Param("page_size") int nPageSize);
	
	//
	public List<String> getPushGroupDeviceSeqList(@Param("group_id") String strGroupId);
	public List<DeviceIdInfo> getPushGroupDeviceListEx(@Param("group_id") String strGroupId);
	public List<DeviceTokenExInfo> getPushGroupDeviceTokenExList(@Param("group_id") String strGroupId);
	public List<DeviceTokenExInfo> getGcmGroupDeviceTokenExList(@Param("group_id") String strGroupId);
	public List<DeviceTokenExInfo> getApnsGroupDeviceTokenExList(@Param("group_id") String strGroupId);
	
	//
	public void createGroup(GroupWorkInfo info);
	public void deleteGroup(@Param("group_id") String strGroupId);
	public List<String> getPushGroupList(@Param("push_user_seq") int nUserSeq);
	
	//
	public void addDeviceSeqToGroup(@Param("group_id") String strGroupId, @Param("device_seq") int nDeviceSeq);
	public void removeDuplicateSeqFromGroup(@Param("group_id") String strGroupId);
	//
	public void deleteDeviceSeqFromGroup(@Param("group_id") String strGroupId, @Param("device_seq") int nDeviceSeq);
	
	////
	public void createGcmGroup(GroupWorkInfo info);
	public void deleteGcmGroup(GroupWorkInfo info);
	public List<String> getGcmGroupList(GroupWorkInfo info);
	//
	public String getGcmGroupData(GroupWorkInfo info);
	public void updateGcmGroupData(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName, @Param("group_id") String strGroupId, 
			@Param("seq_list") List<Integer> arrDeviceSeq);
	public void deleteGcmGroupData(GroupWorkInfo info);
	// device_seq 리스트
	public List<Integer> getGcmDeviceSeqList(List<String> arrDeviceId);
	public String getGcmGroupDeviceSeqList(GroupWorkInfo info);
	//
	public List<Integer> getGcmDeviceTokenList(List<String> arrDeviceToken);
	//
	public void createApnsGroupId(GroupWorkInfo info);
	public void deleteApnsGroupId(GroupWorkInfo info);
	public void updateApnsGroupData(GroupWorkInfo info);
	// device_seq 리스트
	public String getApnsGroupDeviceSeqList(GroupWorkInfo info);
	//
	
	//
	//registerApnsPush
	public void registerApnsPush(RegisterApnsInfo info);
	//
	public RegisterApnsInfo getApnsCertInfo(@Param("user_seq") int nUserSeq, @Param("push_name") String strPushName);
	public List<String> getAllApnsDeviceTokenList(String strAppId);
	// save message
	public void putMessage(PushMessageInfo info);
	public void updateMessageSendCount(@Param("m_id") long m_id, @Param("send_cnt") int send_cnt);
	public void addMessageCount(long m_id);
	//
	public void updateMessageSendConfirm(@Param("m_id") long m_id, @Param("device_seq") int device_seq);
	public void confirmMessage(@Param("m_id") long m_id, @Param("device_seq") int device_seq);
	//
	public int getPushUserSeqA(String strProjNum);
	public int getPushUserSeqI(String strAppId);
	public void addCustomerMessage(CustomerMessageInfo info);
	public String getUserData(long m_id);
	
	//
	public void insertDeviceMap(DeviceMapInfo info);
	public void updateDeviceMap(DeviceMapInfo info);
	
	//
	public void deleteDeviceMap(DeviceMapInfo info);
	
	//
	public CommonDeviceInfo getDeviceInfo(String strExternalID);
	public String getDeviceTokenA(CommonDeviceInfo info);
	public String getDeviceTokenI(CommonDeviceInfo info);
	
	// 통합 로그인 개념 관련 내용
	public int hasExternalID(String strExternalID);
	public int hasSameAExternalID(@Param("device_id") String strDevicelID, @Param("device_token") String strDevicelToken);
	public int hasSameIExternalID(@Param("device_id") String strDevicelID, @Param("device_token") String strDevicelToken);
	
	/////////////////////////////////////////////////////////////////////////
	// 미적용 개발중인 내용들...
//	public ExtSiteDeviceInfo getDeviceTokenA(String strSiteId);
//	public ExtSiteDeviceInfo getDeviceTokenI(String strSiteId);
//	
	//
	public List<String> getExtDeviceListA(@Param("ext_id_list") List<String> ext_id_list);
	public List<String> getExtDeviceListI(@Param("ext_id_list") List<String> ext_id_list);
}
