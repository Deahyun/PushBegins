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
	String push_name = request.getParameter("push_name");
	String proj_num = request.getParameter("proj_num");
	String api_key = request.getParameter("api_key");

	if ( is_ajax == null ) return;
	if ( email == null || email.length() == 0 ) return;
	if ( push_name == null || push_name.length() == 0 ) return;
	if ( proj_num == null || proj_num.length() == 0 ) return;
	if ( api_key == null || api_key.length() == 0 ) return;
	
	RegisterGcmInfo info = new RegisterGcmInfo();
	info.email = email;
	info.push_name = push_name;
	info.proj_num = proj_num;
	info.api_key = api_key;
	
	int nResult = 0;
	String strError = null;
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//userMapper.registerGcmPush(info);
		userMapper.registerGcmPush(email, push_name, proj_num, api_key);
		sqlSession.commit();
		nResult = 1;
		
	} catch ( Exception e ) {
		strError = e.getMessage();
		nResult = 0;
		System.out.println(strError);
		
	} finally {
		sqlSession.close();
	}
	
	if ( nResult == 1 ) {
		out.print("ok");
	} else {
		//out.print(strError);
		out.print("failure");
	}
	out.flush();
%>