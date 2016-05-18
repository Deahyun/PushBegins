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
	
	String group_id = request.getParameter("group_id");
	String user_seq = request.getParameter("user_seq");
	String push_name = request.getParameter("push_name");

	if ( is_ajax == null ) return;
	if ( group_id == null || group_id.length() == 0 ) return;
	if ( user_seq == null || user_seq.length() == 0 ) return;
	if ( push_name == null || push_name.length() == 0 ) return;

	// public void createGcmGroup(GroupWorkInfo info);
	
	GroupWorkInfo info = new GroupWorkInfo();
	info.group_id = group_id;
	info.push_user_seq = Integer.parseInt(user_seq);
	info.push_name = push_name;
	
	int nResult = 0;
	String strError = null;
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.createGcmGroup(info);
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