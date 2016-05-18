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
<%@ page import="network.*"  %>
<%@ page import="utils.*"  %>

<jsp:useBean id="user_mail" class="network.UserMail" scope="page" />
<jsp:useBean id="random" class="utils.RandomNumber" scope="page" />

<%!
	void removeTimeoutObject(ServletContext application, JspWriter out) {

		Enumeration attrEnum = application.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String name = (String) attrEnum.nextElement();
			if (name.length() > 4) {
				String cmp = name.substring(0, 4);
				if (cmp.compareTo("____") == 0) {
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

						// delete old than 10 minutes
						int nDelta = Integer.parseInt(strCurrentTime) - Integer.parseInt(strTime);
						if (nDelta > 600) {
							application.removeAttribute(name);
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

<%
	String tag = "email_code_req";
	String request_type = request.getParameter("request_type");
	String email = request.getParameter("email");
	
	if ( request_type == null || request_type.length() == 0 ) return;
	if ( email == null || email.length() == 0 ) return;
	//
	String strResult = "failure";
	//
	if ( request_type.equals("code_request") ) {
		long now = System.currentTimeMillis();
		long nTime = now / 1000;
		String strTime = String.format("%d", nTime);

		//SimpleDateFormat df = new SimpleDateFormat("HHmm");
		//String strTime = df.format(new java.util.Date());

		String strAppKey = "____" + email;
		String strRandom = random.getRandom();
		String strValue = strRandom + ":" + strTime;

		// application 에 저장되는 형식
		// [ ____이메일]     => [인증번호:시간숫자]
		// [____a@b.com] => [234567:123456789]
		application.setAttribute(strAppKey, strValue);

		//String strRandom = random.getRandom();
		String contents = String.format(
				"인증코드: %s<br>\n이 코드값은 10분간 유효합니다.", strRandom);
		
		String res = user_mail.sendMail(email, "인증코드", contents);
		
		if ( res != null ) {
			if ( res.compareTo("yes") == 0 ) {
				//session.setAttribute(info.email, strRandom);
				//session.setMaxInactiveInterval(3); // minute(s)
				strResult = "success";
			}
		}
		
	} else if ( request_type.equals("code_validate") ) {
		String validate_code = request.getParameter("validate_code");
		
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
			strResult = "success";
			application.removeAttribute(strAppKey);
		}
		//
		
		// 10분 동안 미사용 Application 항목 제거
		removeTimeoutObject(application, out);
		
	} else {
		//
	}

	//
	UserLog.Log(tag, strResult);
	//
	out.print(strResult);
	out.flush();
%>