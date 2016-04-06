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

<%! List<CustomerMessageInfo> getCustomerMessageList(String strUserSeq) {
	
	List<CustomerMessageInfo> resList = null;
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
		resList = userMapper.getCustomerMessageList(nUserSeq, nStartPos, nPageSize);

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
<title>사용자 피드백(feedback) 메시지 목록</title>
</head>
<body>
사용자 피드백(feedback) 메시지 목록<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>사용자 피드백(feedback) 메시지 목록</li>
<ul>
<%
	List<CustomerMessageInfo> resList = getCustomerMessageList(user_seq);
	for ( int i = 0; i < resList.size(); i++ ) {
		CustomerMessageInfo info = resList.get(i);
		String[] arrItem = info.device_id.split("\\|");
		if ( arrItem.length != 3 )
			continue;
		String strText = String.format("(%s) %s | %s / message: %s",
				arrItem[0], arrItem[2], info.reg_tm, info.message);
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>