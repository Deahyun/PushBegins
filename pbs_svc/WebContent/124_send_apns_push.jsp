<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="header.jsp" %>
<%
	String strPushName = request.getParameter("push_name");
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
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).ready( function() {
	//		
	$("#register").click( function() {
		var user_id = $("#user_id").val();
		var push_name = $("#push_name").val();
		var push_alert = $("#push_alert").val();
		var push_message = $("#push_message").val();
		if ( user_id.length == 0 || push_name.length == 0 || push_alert.length == 0 || push_message.length == 0 ) {
			$("#message").html("<font color=blue>필요한 값이 없습니다.</font>");
			setTimeout(function() { 
				$("#message").html("");
			}, 3000);
			return false;
		}
		if (  push_message.length >= 1000 ) {
			$("#message").html("<font color=blue>보낼 내용의 글자는 1000 글자 이내여야 합니다.</font>");
			setTimeout(function() { 
				$("#message").html("");
			}, 3000);
			return false;
		}
		//
		//var json_text = JSON.stringify(json_data);
		//
		var action = $("#form1").attr('action');
		//
		var json_data = new Object();
		//
		json_data["io_kind"] = "send_apns_all_push_req";
		json_data["user_id"] = user_id;
		json_data["push_name"] = push_name;
		//
		//var msg_id = new Date().getTime();
		//json_data["msg_id"] = msg_id;
		//
		var msg_data = new Object();
		msg_data["alert"] = push_alert;
		msg_data["badge"] = 1;
		msg_data["sound"] = "default";
		msg_data["user_data"] = push_message;
		//
		json_data["json_data"] = msg_data;
		//

	$.ajax({
		type: "POST",
		url: action,
		data: JSON.stringify(json_data),
		contentType: 'application/json; charset=utf-8',
	    dataType: 'json',
		success: function(response) {
			//var jsObj = JSON.parse(response);
			var result = response.result;
			if ( result == "yes" ) {
				$("#message").html("<font color=blue>apple push 를 전송했습니다.</font>");
				setTimeout(function() {
					$("#push_alert").val("");
					$("#push_message").val("");
					$("#message").html("");
				}, 5000);
				//
			} else {
				$("#message").html("<font color=red>apple push 를 전송중 오류가 발생 했습니다.</font>");
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
		<form id="form1" action="Listener" method="post">
		<input type="hidden" id="user_id" value="<%=strUserId%>">
		<input type="hidden" id="push_name" value="<%=strPushName%>">
		<fieldset>
			<legend>APPLE PUSH 보내기</legend>
			<label for="push_alert">Push Alert: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><input type="text" id="push_alert" />
			<label for="push_message">Push User Data: </label>
			<textarea rows="10" cols="50" id="push_message" onkeypress="if (this.value.length >= 1000) { return false; }"></textarea>
			<input type="button" value="apple push 보내기" id="register" />
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