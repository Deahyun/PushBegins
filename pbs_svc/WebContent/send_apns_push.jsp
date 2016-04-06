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
<title>Apple Push 보내기</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="css/send_gcm_push.css" media="all" />
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
<!-- <input type="text" id="push_message" /> -->
	<form id="form1" action="Listener" method="post">
		<input type="hidden" id="user_id" value="<%=strUserId%>">
		<input type="hidden" id="push_name" value="<%=strPushName%>">
		<fieldset>
			<legend>APPLE PUSH 보내기</legend>
			<label for="push_alert">Push Alert: </label><input type="text" id="push_alert" />
			<label for="push_message">Push User Data: </label>
			<textarea rows="10" cols="50" id="push_message" onkeypress="if (this.value.length >= 1000) { return false; }"></textarea>
			<input type="button" value="apple push 보내기" id="register" />
		</fieldset>
	</form>
	<div><p id="message"></p></div>
</body>
</html>