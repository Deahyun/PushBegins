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
List<RegisterGcmInfo> getPushList(String strUserSeq) {
	
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

List<String> getGcmPushGroupList(String strUserSeq, String strPushName) {
	//
	List<String> resList = null;
	//
	GroupWorkInfo info = new GroupWorkInfo();
	try {
		info.push_user_seq = Integer.parseInt(strUserSeq);
		info.push_name = strPushName;
		
	} catch ( Exception e ) {
		//
		return resList;
	}
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		resList = userMapper.getGcmGroupList(info);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

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
<title>google push 정보 관리</title>
</head>
<body>
google push 정보 관리<br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push app 목록</li>
<ul>
<%
	List<RegisterGcmInfo> resList = getPushList(strUserSeq);
	for ( int i = 0; i < resList.size(); i++ ) {
		RegisterGcmInfo info = resList.get(i);
		// push 정보삭제시 해당 push 이름으로 배포된 모든 안드로이드/아이폰 클라이언트 가 
		// 동작하지 않으므로, push 정보 삭제는 관리자의 권한으로만 삭제할 수 있게 처리해야 할듯.
		String strText = String.format("<font color=green>%s</font> ( project number: %s | api key: %s )<br>"
				+ " <a href=\"create_gcm_group.jsp?push_name=%s\">group 생성<a> ",
				info.push_name, info.proj_num, info.api_key, info.push_name);
		out.println("<li>" + strText + "</li>");
		//if ( true ) continue;
		//
		List<String> groupList = getGcmPushGroupList(strUserSeq, info.push_name);
		for ( int j = 0; j < groupList.size(); j++ ) {
			String strGroup = String.format("<font color=red>%s (사용자 추가/삭제 및 그룹삭제)</font>",  
					groupList.get(j));
			out.println("<ul>" + strGroup + "</ul>");
		}
	}
%>
</ul>
</ul>
</body>
</html>