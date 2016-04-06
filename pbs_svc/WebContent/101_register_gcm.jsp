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
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).ready( function() {
	//		
	$("#register").click( function() {
		var push_name = $("#push_name").val();
		var proj_num = $("#proj_num").val();
		var api_key = $("#api_key").val();
		if ( proj_num.length == 0 || api_key.length == 0 || push_name.length == 0 ) {
			$("#message").html("<font color=blue>모든 항목을 채워주세요</font>");
			setTimeout(function() { 
				$("#message").html("");
			}, 3000);
			return false;
		}
		var action = $("#form1").attr('action');
		var form_data = {
			email: $("#email").val(),
			push_name: $("#push_name").val(),
			proj_num: $("#proj_num").val(),
			api_key: $("#api_key").val(),
			is_ajax: 1
		}; // var
	$.ajax({
		type: "POST",
		url: action,
		data: form_data, 
		success: function(response) {
			//alert(response);
			if ( response == 'ok' ) {
				$("#form1").slideUp('normal', function() {
					window.location.href = "push_usage.jsp";
				  });
			} else {
				alert("[" + response + "]");
				$("#message").html("<font color=blue>push 정보 등록이 실패했습니다.</font>");
				setTimeout(function() {
					$("#message").html("");
				}, 3000);
			}
		} // function
		}); // $.ajax
		return false;
	}); // click(function()

	////
	
	////
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
		<form id="form1" action="register_gcm_req.jsp" method="post">
		<input type="hidden" id="email" value="<%=strUserId%>">
		<fieldset>
			<legend>GOOGLE PUSH 정보 등록</legend>
			<label for="push_name">Push App Name: </label><input type="text" id="push_name" />
			<label for="proj_num">Project Number: </label><input type="text" id="proj_num" />
			<label for="api_key">Server API Key: </label><input type="text" id="api_key" />
			<input type="button" value="GCM 정보 등록" id="register" />
		</fieldset>
		</form>
		<div><p id="message"></p></div>

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