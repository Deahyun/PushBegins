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
    
<%@include file="header.jsp" %>

<%! List<String> getAppList(String strUserSeq) {
	
	List<String> resList = null;
	//
	int nStartPos = 0;
	int nPageSize = 50;
	//
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getAppList(nUserSeq, nStartPos, nPageSize);

	} catch (Exception e) {
		e.printStackTrace();

	} finally {
		sqlSession.close();
	}

	return resList;
}%>

<%
	String user_seq = request.getParameter("user_seq");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>push 메시지 목록</title>
</head>
<body>
push app 목록<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push app 목록</li>
<ul>
<%
	List<String> resList = getAppList(user_seq);
	for ( int i = 0; i < resList.size(); i++ ) {
		String push_name = resList.get(i);
		String strText = String.format("<a href=\"push_device_list.jsp?user_seq=%s&push_name=%s\">%s</a> ", 
				user_seq, push_name, push_name);
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>