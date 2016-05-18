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
<title>PushBegins Admin Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link href="css/dash-board.css" rel="stylesheet">
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<!--[if lt IE 9]>alert(form_data);
	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<script type="text/javascript">
	$(document).ready( function() {
				
	}); // ready(function()
</script>
</head>
<body>
<div class="wrapper">
		<!-- start header  -->
		<header>
		<h1>dummy</h1>
		</header>
		<!-- end header  -->

		<!-- start aside  -->
		<aside>
		<!-- start section  -->
		<section class="popular-recipes">
			<a href="100_register_push.jsp">app 의 push 관련 정보 등록</a>
			<a class="current">app 의 그룹 관리</a>
			<a href="120_send_all_push.jsp">전체 push 메시지 보내기</a>
			<a href="130_device_list.jsp">앱을 설치한 스마트폰 보기</a>
			<a href="140_push_result_list.jsp">보낸 push 메시지 목록보기</a>
			<a href="150_feedback_list.jsp">사용자 feedback 메시지보기</a>
			<a href="160_push_api_usage.jsp">API 방식 push 안내</a>
		</section>
		<!-- end section  -->
		<!-- start section  -->
		<section class="contact-details">
			<h2>Contact</h2>
			<!-- <div id="contact"></div> -->
			<p>
			If you have any question <br/>
			email to
			<font color="#de6581">id@entertera.com</font>
			</p>
		</section>
		<!-- start section  -->
		</aside>
		<!-- end aside  -->
		
		<!-- start section  -->
		<section class="counselor">
		<ul>
			<h2><font color="green">push group 정보 관리</font></h2><p>
		</ul>
		<ul>
			<p id="message"></p>
		</ul>
<!--		
		<ul>
			<li><a href="push_group_list.jsp?user_seq=<%=strUserSeq%>">push 앱 그룹 관리</a></li>
		</ul>
-->
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
				
				String strText = String.format("<font color=green><a href=\"111_create_group.jsp?push_user_seq=%s&push_name=%s\">group 생성<a></font> ",
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
					String strGroup = String.format("<a href=\"112_edit_group.jsp?push_user_seq=%s&push_name=%s&group_id=%s\">%s (사용자 추가/삭제 및 그룹삭제)</a>",  
							strUserSeq, push_name, groupList.get(j), groupList.get(j));
					out.println("<ul>" + strGroup + "</ul>");
				}
			}
		%>
		</ul>
		</ul>
		
		</section>
		<!-- end section  -->
		
		<!-- start section  -->
		<section class="save_buttons">
		</section>
		<!-- end section  -->
		
		<footer> &copy; 2016 EnterTera Inc. All rights reserved. </footer>
	</div>

</body>
</html>