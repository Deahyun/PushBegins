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

<%! List<RegisterApnsInfo> getPushList(String strUserSeq) {
	
	List<RegisterApnsInfo> resList = null;
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getApnsPushList(nUserSeq);

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
<title>apple push 정보 관리</title>
</head>
<body>
apple push 정보 관리<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push 목록</li>
<ul>
<%
	List<RegisterApnsInfo> resList = getPushList(user_seq);
	for ( int i = 0; i < resList.size(); i++ ) {
		RegisterApnsInfo info = resList.get(i);
		//
		String strText = String.format("<font color=green>%s</font> ( app_id: %s )<br>"
		+ " push정보삭제 group등록 group삭제 group관리(사용자 추가/삭제) ", 
				info.push_name, info.app_id);
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>