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

<%! List<PushMessageInfo> getPushMessageList(String strUserSeq) {
	
	List<PushMessageInfo> resList = null;
	//
	int nStartPos = 0;
	int nPageSize = 20;
	//
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getPushMessageList(nUserSeq, nStartPos, nPageSize);

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
push 메시지 목록<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push 메시지 목록</li>
<ul>
<%
	List<PushMessageInfo> resList = getPushMessageList(user_seq);
	for ( int i = 0; i < resList.size(); i++ ) {
		PushMessageInfo info = resList.get(i);
		String type = "android";
		if ( info.message_type.equals("i") )
			type = "iOS";
		
		String strText = String.format("[%s] alert: %s<br> "
				+ "total_cnt: %d / send_cnt: %d / 수신확인: %d / %s",
				type, info.alert, info.total_cnt, info.send_cnt, info.success_cnt, info.reg_tm);
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>