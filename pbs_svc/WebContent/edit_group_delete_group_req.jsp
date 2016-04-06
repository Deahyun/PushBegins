<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="java.util.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Reader"%>
<%@page import="com.site.config.*"%>
<%@page import="com.site.dao.*"%>
<%@page import="com.site.entity.*"%>
<%@page import="com.site.sqlmap.*"%>
<%@ page import="org.apache.ibatis.session.*" %>

<%@page import="org.json.simple.*"%>

<%@ page import="com.entertera.info.*"  %>
<%@ page import="utils.*"  %>

<%
	String strError = "parameter not found";
	String tag = "edit_group_delete_group";
	SqlSession sqlSession = null;
	
	boolean bResult = false;
	
	String strGroupId = request.getParameter("group_id");
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.deleteGroup(strGroupId);
		sqlSession.commit();
		bResult = true;
	
	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);
	
	} finally {
		sqlSession.close();
	}
	
	if ( bResult ) {
		out.print("ok");
		
	} else {
		out.print("failure");
	}
	
	out.flush();
%>