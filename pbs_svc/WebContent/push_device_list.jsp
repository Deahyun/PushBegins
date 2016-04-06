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

<%! List<String> getPushDeviceList(String strUserSeq, String strPushName) {
	
	List<String> resList = null;
	//
	int nUserSeq = Integer.parseInt(strUserSeq);
	//
	int nStartPos = 0;
	int nPageSize = 50;
	//
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		//
		String strProjNum = userMapper.getGcmProjNum(nUserSeq, strPushName);
		String strAppId = userMapper.getApnsAppId(nUserSeq, strPushName);
		//
		resList = userMapper.getPushDeviceList(strProjNum, strAppId, nStartPos, nPageSize);

	} catch (Exception e) {
		e.printStackTrace();

	} finally {
		sqlSession.close();
	}

	return resList;
}%>

<%
	String user_seq = request.getParameter("user_seq");
	String push_name = request.getParameter("push_name"); 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>push 기기 목록</title>
</head>
<body>
push 기기 목록<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push 이름: <%= push_name %></li>
<p>
<li>push 기기 목록</li>
<ul>
<%
	List<String> resList = getPushDeviceList(user_seq, push_name);
	for ( int i = 0; i < resList.size(); i++ ) {
		String device_id = resList.get(i);
		String[] arrItem = device_id.split("\\|");
		if ( arrItem.length != 3 )
			continue;
		String strText = String.format("(%s) %s", 
				arrItem[0], arrItem[2]); 
		out.println("<li>" + strText + "</li>");
	}
%>
</ul>
</ul>
</body>
</html>