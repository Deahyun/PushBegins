<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

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
			<a class="current">app 의 push 관련 정보 등록</a>
			<a href="counselor_search.jsp">app 의 그룹 관리</a>
			<a href="counselor_register.jsp">전체 push 메시지 보내기</a>
			<a href="review_list.jsp">앱을 설치한 스마트폰 보기</a>
			<a href="server_list.jsp">보낸 push 메시지 목록보기</a>
			<a href="set_charge.jsp">사용자 feedback 메시지보기</a>
			<a href="upload_html.jsp">API 방식으로 push 보내기</a>
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
			<h2><font color="green">push 관련 정보 등록</font></h2><p>
		</ul>
		<ul>
			<p id="message"></p>
		</ul>
		
		</section>
		<!-- end section  -->
		
		<!-- start section  -->
		<section class="save_buttons">
			<div class="btn_action">
				<p class="va_upload">
				<input type="button" id="photo_upload_button"  class="exec_btn" value="상담사 정보 등록" />
				<input type=button id="cancel_btn" class="cancel_btn" value="취소">
				</p>
			</div>
		</section>
		<!-- end section  -->
		
		<footer> &copy; 2016 EnterTera Inc. All rights reserved. </footer>
	</div>



<h3>PushBegins 기본메뉴</h3><br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>

<li>app 의 push 관련 정보 등록</li>
<ul>
	<li><a href="register_gcm.jsp">google push(GCM) 정보등록</a></li>
	<li><a href="gcm_push_list.jsp?user_seq=<%=strUserSeq%>">등록된 google push(GCM) 정보 보기</a></li>
	<li><a href="register_apns.jsp">apple push(APNS) 정보등록</a></li>
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">등록된 apple push(APNS) 정보 보기</a></li>
</ul>
<p>

<li>app 의 그룹 관리</li>
<ul>
<!--
	<li><a href="gcm_push_group_list.jsp?user_seq=<%=strUserSeq%>">android push 앱 그룹 관리</a></li>
	<li><a href="apns_push_group_list.jsp?user_seq=<%=strUserSeq%>">ios push 앱 그룹 관리</a></li>
-->
	<li><a href="push_group_list.jsp?user_seq=<%=strUserSeq%>">push 앱 그룹 관리</a></li>
	<!--
	<li><a href="apns_push_group_list.jsp?user_seq=<%=strUserSeq%>">통합 push 앱 그룹 관리</a></li>
	-->
</ul>
<p>

<li>기기종류별 전체 push 메시지 보내기</li>
<ul>
	<li><a href="gcm_push_list2.jsp?user_seq=<%=strUserSeq%>">google push 보내기</a></li>
	<li><a href="apns_push_list2.jsp?user_seq=<%=strUserSeq%>">apple push 보내기</a></li>
	<!--
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">통합 push 선택(push-name 으로 일괄전송)</a></li>
	-->
</ul>
<p>

<li>등록된 기기(앱을 설치한 스마트폰) 보기</li>
<ul><a href="app_list.jsp?user_seq=<%=strUserSeq%>">app 종류별 기기 목록</a></ul>
<p>

<li>보낸 push 메시지 목록보기</li>
<ul>
	<!--
	<li><a href="gcm_push_list.jsp?user_seq=<%=strUserSeq%>">google push 메시지 목록</a></li>
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">apple push 메시지 목록</a></li>
	-->
	<li><a href="push_message_list.jsp?user_seq=<%=strUserSeq%>">전체 push 메시지 목록</a></li>
</ul>
<p>

<li>사용자 피드백(feedback) 메시지보기 </li>
<ul>
	<li><a href="feedback_message_list.jsp?user_seq=<%=strUserSeq%>">피드백(feedback) 메시지 목록</a></li>
</ul>
<p>

<li>API 방식으로 push 보내는 방법</li>
<ul>
	<li>GCM 과 APNS 를 동일한 방식으로 보낸다.</li>
	<li>push 전송을 위해 접속하는 url 은 http(s)://사용자_url:사용자_port/pbs_svc/Listener 이 된다.</li>
	<li></li>
	<li>아래는 push 사용자가 teraget@gmail.com 이고 등록된 push 이름이 pushbegins 인  안드로이드 기기(proj_num 은 [기기종류별 전체 push 메시지 보내기] 에서 참고)에 push 를 보내는 json API 예제이다.</li>
	<li>'{"io_kind":"send_push_req", "user_id":"teraget@gmail.com", "push_name":"pushbegins", \
"device_id_ex":"a|515463805231|01012345678'", \
"json_data":{"alert":"This is title", "user_data":"This is long message"}}'</li>
	<li></li>
	<li>아래는 push 사용자가 teraget@gmail.com 이고 등록된 push 이름이 pushbegins 인 아이폰 기기(app_id 는 [기기종류별 전체 push 메시지 보내기] 에서 참고)에 push 를 보내는 json API 예제이다.</li>
	<li>'{"io_kind":"send_push_req", "user_id":"teraget@gmail.com", "push_name":"pushbegins", \
"device_id_ex":"i|com.entertera.pushbegins|01012345678'", \
"json_data":{"alert":"This is title", "user_data":"This is long message"}}'</li>
</ul>
<p>

</body>
</html>