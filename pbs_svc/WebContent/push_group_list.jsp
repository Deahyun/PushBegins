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

<%!
String tag =  "push_group_list";
List<RegisterGcmInfo> getGcmPushList(String strUserSeq) {
	
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
}

List<String> getPushGroupList(String strUserSeq) {
	//
	List<String> resList = null;
	//
	int nUserSeq = 0;
	try {
		nUserSeq = Integer.parseInt(strUserSeq);
		
	} catch ( Exception e ) {
		//
		UserLog.Log(tag, "no result found");
		return resList;
	}
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		resList = userMapper.getPushGroupList(nUserSeq);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return resList;
}

List<String> getAppList(String strUserSeq) {
	
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
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>push group 정보 관리</title>
</head>
<body>
push group 정보 관리<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push app 목록</li>
<!--
해당 push user 와 연관이 있는 push app 목록을 전부 보여준다.<br>
안드로이드용 push 상세정보(project number / api key) 및 아이폰 push 상세정보(app_id) 보기를 추가한다.
 -->
 <font color="#665544">동일한 group_id 를 가진 그룹은 1개만 생성할 수 있습니다.</font>
<ul>
<%
	//List<String> resList = getPushGroupList(strUserSeq);
	List<String> resList = getAppList(strUserSeq);
	for ( int i = 0; i < resList.size(); i++ ) {
		String push_name = resList.get(i);
		out.println("<li>" + "app 이름: <font color=red>" + push_name + "</font></li>");
		
		String strText = String.format("<font color=green><a href=\"create_group.jsp?push_user_seq=%s&push_name=%s\">group 생성<a></font> ",
				strUserSeq, push_name);
		out.println("<li>" + strText + "</li>");
		
		
		// push 정보삭제시 해당 push 이름으로 배포된 모든 안드로이드/아이폰 클라이언트 가 
		// 동작하지 않으므로, push 정보 삭제는 관리자의 권한으로만 삭제할 수 있게 처리해야 할듯.
		//String strText = String.format("<font color=green><a href=\"create_group.jsp?push_user_seq=%s&push_name=%s\">group 생성<a></font> ",
		//		strUserSeq, info.push_name);
		//out.println("<li>" + strText + "</li>");
		//if ( true ) continue;
		//
		List<String> groupList = getPushGroupList(strUserSeq);
		for ( int j = 0; j < groupList.size(); j++ ) {
			String strGroup = String.format("<a href=\"edit_group.jsp?push_user_seq=%s&push_name=%s&group_id=%s\">%s (사용자 추가/삭제 및 그룹삭제)</a>",  
					strUserSeq, push_name, groupList.get(j), groupList.get(j));
			out.println("<ul>" + strGroup + "</ul>");
		}
	}
%>
</ul>
</ul>
</body>
</html>