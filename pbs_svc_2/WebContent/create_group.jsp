<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>

<%
	String strPushName = request.getParameter("push_name");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Google Push 그룹 생성</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="css/register.css" media="all" />
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).ready( function() {
	//		
	$("#register").click( function() {
		var group_id = $("#group_id").val();
		var user_seq = $("#user_seq").val();
		var push_name = $("#push_name").val();
		if ( group_id.length == 0 || user_seq.length == 0 || push_name.length == 0 ) {
			$("#message").html("<font color=blue>모든 항목을 채워주세요</font>");
			setTimeout(function() { 
				$("#message").html("");
			}, 3000);
			return false;
		}
		var action = $("#form1").attr('action');
		var form_data = {
			group_id: $("#group_id").val(),
			user_seq: $("#user_seq").val(),
			push_name: $("#push_name").val(),
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
					window.location.href = "push_group_list.jsp";
					//location.reload();
				  });
			} else {
				alert("[" + response + "]");
				$("#message").html("<font color=blue>push 그룹 생성이 실패했습니다.</font>");
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
	<form id="form1" action="create_group_req.jsp" method="post">
		<input type="hidden" id="user_seq" value="<%=strUserSeq%>">
		<input type="hidden" id="push_name" value="<%=strPushName%>">
		<fieldset>
			<legend>PUSH 그룹 생성</legend>
			<label for="group_id">그룹 ID: </label><input type="text" id="group_id" />
			<input type="button" value="그룹생성" id="register" />
		</fieldset>
	</form>
	<div><p id="message"></p></div>
</body>
</html>