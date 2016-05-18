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
			<!-- <a class="current">app 의 push 관련 정보 등록</a> -->
			<a href="100_register_push.jsp">app 의 push 관련 정보 등록</a>
			<a href="110_manage_group.jsp">app 의 그룹 관리</a>
			<a href="120_send_all_push.jsp">전체 push 메시지 보내기</a>
			<a href="130_device_list.jsp">앱을 설치한 스마트폰 보기</a>
			<a href="140_push_result_list.jsp">보낸 push 메시지 목록보기</a>
			<a href="150_feedback_list.jsp">사용자 feedback 메시지보기</a>
			<a class="current">API 방식 push 안내</a>
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
			<h2><font color="green">API 방식 push 안내</font></h2><p>
		</ul>
		<ul>
			<p id="message"></p>
		</ul>
		
		<ul>
			<li>GCM 과 APNS 를 동일한 방식으로 보낸다.</li>
			<li>push 전송을 위해 접속하는 url 은 http(s)://사용자_url:사용자_port/pbs_svc/Listener 이 된다.</li>
			<li>pushbegins 사이트의 url 은 [http://www.pushbegins.com:18080/pbs_svc/Listener]</li>
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