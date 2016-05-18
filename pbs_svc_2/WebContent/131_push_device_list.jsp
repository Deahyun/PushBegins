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
<title>PushBegins Admin Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link href="css/dash-board.css" rel="stylesheet">
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<!--[if lt IE 9]>alert(form_data);
	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
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
			<a href="110_manage_group.jsp">app 의 그룹 관리</a>
			<a href="120_send_all_push.jsp">전체 push 메시지 보내기</a>
			<a class="current">앱을 설치한 스마트폰 보기</a>
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
			<h2><font color="green">push 기기 목록</font></h2><p>
		</ul>
		<ul>
			<p id="message"></p>
		</ul>
		
		<ul>
		<li>사용자: <%= strUserId %></li>
		<p>
		<li>push app 이름: <%= push_name %></li>
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