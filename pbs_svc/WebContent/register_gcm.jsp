<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Google Push 등록</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="css/register.css" media="all" />
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
</body>
</html>