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

<%! List<RegisterGcmInfo> getPushList(String strUserSeq) {
	
	List<RegisterGcmInfo> resList = null;
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getGcmPushList(nUserSeq);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

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
<title>google push 보내기</title>
</head>
<body>
google push 보내기<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push app 목록</li>
<ul>
<%
	List<RegisterGcmInfo> resList = getPushList(user_seq);
	for ( int i = 0; i < resList.size(); i++ ) {
		RegisterGcmInfo info = resList.get(i);
		String strText = String.format("<font color=green>%s</font> ( project number: %s | api key: %s )<br>"
				+ " <a href=send_gcm_push.jsp?push_name=%s>push 보내기</a> ",
				info.push_name, info.proj_num, info.api_key, info.push_name);
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>