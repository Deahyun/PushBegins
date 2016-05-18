<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="java.util.*"%>
<%@page import="java.io.Reader"%>
<%@page import="com.site.config.*"%>
<%@page import="com.site.dao.*"%>
<%@page import="com.site.entity.*"%>
<%@page import="com.site.sqlmap.*"%>
<%@ page import="org.apache.ibatis.session.*" %>

<%@ page import="com.entertera.info.*"  %>
<%@ page import="utils.*"  %>
<%@ page import="network.*"  %>

<jsp:useBean id="user_mail" class="network.UserMail" scope="page" />
<jsp:useBean id="random" class="utils.RandomNumber" scope="page" />

<%! boolean deleteGcmUser(String email) {
	
	boolean bResult = false;
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.deleteGcmUser(email);
		sqlSession.commit();
		bResult = true;

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return bResult;
}%>

<%! boolean activateGcmUser(String email) {
	
	boolean bResult = false;
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.activateGcmUser(email);
		sqlSession.commit();
		bResult = true;

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return bResult;
}%>

<%!
	void removeTimeoutObject(ServletContext application, JspWriter out) {

		// application 에 저장되는 형식
		// [ ____이메일]     => [인증번호:시간숫자]
		// [____a@b.com] => [234567:123456789]
			
		Enumeration attrEnum = application.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String name = (String) attrEnum.nextElement();
			if (name.length() > 4) {
				String cmp = name.substring(0, 4);
				//
				if (cmp.compareTo("____") == 0) {
					//
					String email = name.substring(4, name.length());
					//System.out.println("email -> [" + email + "]");
					//
					try {
						Object value = application.getAttribute(name);
						String strData = (String) value;
						String[] arr = strData.split(":");
						String strNumber = arr[0];
						String strTime = arr[1];

						long now = System.currentTimeMillis();
						long nTime = now / 1000;
						String strCurrentTime = String.format("%d", nTime);

						//SimpleDateFormat df = new SimpleDateFormat("HHmm");
						//String strCurrentTime = df.format(new java.util.Date());

						// delete old than 1 hour
						int nDelta = Integer.parseInt(strCurrentTime) - Integer.parseInt(strTime);
						if (nDelta > 3600) {
							application.removeAttribute(name);
							// 잘못된 사용자의 자동삭제 과정
							// tb_gcm_user 에서 email 을 찾아서 삭제한다.
							deleteGcmUser(email);
						}
						
					} catch (Exception error) {
						error.printStackTrace();
					}
				}
			}
		}
		// while
	}
%>

<%!boolean put_push_message(PushMessageInfo push_msg) {
//	// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
//	PushMessageInfo push_msg = new PushMessageInfo();
//	push_msg.m_id = make_message_id();
//	push_msg.seq = nUserSeq;
//	push_msg.total_cnt = vIosList.size();
//	push_msg.success_cnt = 0;
//	push_msg.message_type = "i";
//	push_msg.user_data = msg.user_data;

	boolean bResult = false;
	////
	SqlSession sqlSession = ConnectionFactory.getSession().openSession();
	//
	try {
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.putMessage(push_msg);
		sqlSession.commit();
		bResult = true;

	} catch ( Exception e ) {
		sqlSession.rollback();
		UserLog.Log("email_validate_test", "put_push_message -> " + e.getMessage());
		//e.printStackTrace();
		//errorReturn(res, out, "9999", e.getMessage());
		//return null;

	} finally {
		sqlSession.close();
	}
	////////
	
	return bResult;
}
%>

<%! boolean sendWelcomePush(String strEmail, String strProjNum) {
	//
	boolean bResult = false;
	KeyTokenInfo info = null;
	String strError = null;
	//
	int nUserSeq = 0;
	//
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		info = userMapper.getGcmKeyToken(strEmail, strProjNum);
		nUserSeq = userMapper.getPushUserSeq(strEmail);
		bResult = true;
		
	} catch ( Exception e ) {
		strError = e.getMessage();
		
	} finally {
		sqlSession.close();
	}
	
	if ( !bResult )
		return bResult;
	
	//
	UserLog.Log("email_validate", "apk key -> [" + info.api_key + "]");
	UserLog.Log("email_validate", "device token -> [" + info.device_token + "]");
	//
	int nTime = UserFunc.now2IntTime();
	String strTime = String.format("%d", nTime);
	//
	String strJsonData = "{\"alert\":\"Welcome to Push Reader\", \"message\":\"Push Reader 앱을 설치해 주셔서 감사합니다.\"}";
	//
	Vector<String> vAndroidList = new Vector<String>();
	vAndroidList.add(info.device_token);
	
	// Send GCM
	GCMClient gcm = new GCMClient();
	gcm.setApiKey(info.api_key);
	
	gcm.setDeviceList(vAndroidList);
	//gcm.sendGCM(strMsgId, strJsonData);
	////////
	// 메시지 전송하기 전 수신확인을 위한 message data 를 기록한다.
	PushMessageInfo push_msg = new PushMessageInfo();
	push_msg.m_id = UserFunc.make_message_id();
	push_msg.seq = nUserSeq;
	push_msg.total_cnt = vAndroidList.size();
	push_msg.success_cnt = 0;
	push_msg.message_type = "a";
	push_msg.user_data = null;
	////

	if ( !put_push_message(push_msg) ) {
		return bResult;
	}
	////
	//gcm.sendGCM(strMsgId, strJsonData);
	int nSendCount = gcm.sendGCM(strJsonData, push_msg.m_id);
	if ( nSendCount > 0 )
		return true;
	else
		return false;
}
%>

<%
	String tag = "email_validate";

	String validate_code = request.getParameter("validate_code");
	String email = request.getParameter("email");
	String proj_num = request.getParameter("proj_num");
	
	if ( validate_code == null || validate_code.length() == 0 ) return;
	if ( email == null || email.length() == 0 ) return;
	if ( proj_num == null || proj_num.length() == 0 ) return;
	//
	
	//
	String strResult = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
			+ "<head>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi\" />"
			+ "<title>PushReader 설치확인</title>"
			+ "</head>"
			+ "<body>"
			+ "<h2>앱 설치확인 이메일 인증을 실패했습니다.</h2>" + "</body>" + "</html>";

	String strSuccess = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
			+ "<head>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi\" />"
			+ "<title>PushReader 설치확인</title>"
			+ "</head>"
			+ "<body>"
			+ "<h2>앱 설치확인 이메일 인증을 성공했습니다. 사용해 주셔서 감사합니다.</h2>"
			+ "</body>" + "</html>";

	//
	String strAppKey = "____" + email;
	Object objValue = application.getAttribute(strAppKey);
	if (objValue == null) {
		out.print(strResult);
		out.flush();
		return;
	}
	String strData = (String) objValue;
	String[] arr = strData.split(":");
	String strNumber = arr[0];
	String strTime = arr[1];
	//

	//
	if (strNumber.compareTo(validate_code) == 0) {
		// tb_gcm_user 의 activity 를 'Y' 로 변경한다.
		activateGcmUser(email);
		strResult = strSuccess;
		application.removeAttribute(strAppKey);
		
		// 가입 축하 push 를 보낸다
		if ( !sendWelcomePush(email, proj_num) ) {
			UserLog.Log(tag, "sendWelcomePush failure");
		}
		
	} else {
		// tb_gcm_user 의 사용자를 삭제한다.
		deleteGcmUser(email);
	}
	//

	// 10분 동안 미사용 Application 항목 제거
	removeTimeoutObject(application, out);
	//
	out.print(strResult);
	out.flush();
%>