<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@page import="java.io.Reader"%>
<%@page import="com.site.config.*"%>
<%@page import="com.site.dao.*"%>
<%@page import="com.site.entity.*"%>
<%@page import="com.site.sqlmap.*"%>
<%@ page import="org.apache.ibatis.session.*" %>
<%
	String is_ajax = request.getParameter("is_ajax");
	String email = request.getParameter("email");
	String pwd = request.getParameter("pwd");
	String alias = request.getParameter("alias");

	if ( is_ajax == null ) return;
	if ( email == null || email.length() == 0 ) return;
	if ( pwd == null || pwd.length() == 0 ) return;
	if ( alias == null || alias.length() == 0 ) return;
	
	PushUserInfo info = new PushUserInfo();
	info.email = email;
	info.pwd = pwd;
	info.alias = alias;
	
	int nResult = 0;
	String strError = null;
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.insertPushUser(info);
		sqlSession.commit();
		nResult = 1;
		
	} catch ( Exception e ) {
		strError = e.getMessage();
		nResult = 0;
		//System.out.println(strError);
	} finally {
		sqlSession.close();
	}
	
	if ( nResult == 1 ) {
		out.print("success");
	} else {
		String strMsg = info.email + " " + info.alias;
		out.print(strError + "\n" + strMsg + " -> " + "failure");
	}
	out.flush();
%>