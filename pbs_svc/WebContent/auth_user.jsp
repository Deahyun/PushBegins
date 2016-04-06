<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="java.io.Reader"%>
<%@page import="com.site.config.*"%>
<%@page import="com.site.dao.*"%>
<%@page import="com.site.entity.*"%>
<%@page import="com.site.sqlmap.*"%>
<%@ page import="org.apache.ibatis.session.*" %>

<%@ page import="utils.UserLog" %>

<%
	String strError = "parameter not found";
	String tag = "auth_user";

	String strUserId = request.getParameter("user_id");
	if ( strUserId == null || strUserId.length() == 0 ) {
		out.println(strError);
		out.flush();
		return;
	}

	String strUserPwd = request.getParameter("user_pwd");
	if ( strUserPwd == null || strUserPwd.length() == 0 ) {
		out.println(strError);
		out.flush();
		return;
	}
	
	int nUserSeq = 0;
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		nUserSeq = userMapper.authPushUser(strUserId, strUserPwd);
		
	} catch ( Exception e ) {
		strError = e.getMessage();
		UserLog.Log(tag, strError);

	} finally {
		sqlSession.close();
	}
	
	if ( nUserSeq > 0 ) {
		session.setAttribute("push_user_id", strUserId);
		session.setAttribute("push_user_seq", nUserSeq);
		out.print("ok");
		
//	} else if ( strUserId.equals("adidas-golf") && strUserPwd.equals("2345") ) {
//			session.setAttribute("company_code", strUserId);
//			out.print("ok");
	} else {
		out.print("failure");
	}

	out.flush();
%>