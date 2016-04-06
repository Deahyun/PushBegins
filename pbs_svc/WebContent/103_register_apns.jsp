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
<link href="css/progress-bar.css" rel="stylesheet">
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="js/vendor/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.iframe-transport.js"></script>
<script type="text/javascript" src="js/jquery.fileupload.js"></script>
<!--[if lt IE 9]>
	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<!-- bootstrap css -->
<!--
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
-->
<!-- bootstrap javascript -->
<script type="text/javascript" src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>

<script type="text/javascript">
	$(document).ready( function() {
		//
		var sandbox = "N";
		$('input:radio[name=sandbox]:input[value='+sandbox+']').attr("checked", true);
		//
		$('#file_btn_cert').click(function() {
        	$('#file_param_1').click();
    	});

		$('#fileupload1').fileupload({
			//
			autoUpload: false,
	    	sequentialUploads: true,
	        dataType: 'json',
			//
			add: function(e, data) {
				//
				var up_file = data.files[0].name;
				$("#file_name_1").val(up_file);
				var up_file_ext = up_file.split(".").pop();
				//
				//
				if ( up_file_ext == "p12" ) {
					// correct
					$("#message").html("");
					
				} else {
					$("#message").html("<font color=red>확장자가 p12 인 인증서 파일만 업로드 할 수 있습니다.</font>");
					setTimeout(function() { 
						$("#message").html("");
					}, 3000);
					$("#file_name_1").val("");
					return;
				}
				//
				var push_name = $("#push_name").val();
				var app_id = $("#app_id").val();
				//var sandbox = "N";
				var cert_pwd = $("#cert_pwd").val();
				if ( push_name.length == 0 || app_id.length == 0 || cert_pwd.length == 0 ) {
					$("#message").html("<font color=red>[Push App Name], [Apple App ID], [인증서 비밀번호]를 입력후 파일을 선택해 주세요.</font>");
					setTimeout(function() { 
						$("#message").html("");
					}, 5000);
					$('#fileupload1')[0].reset();
					return false;
				}
				//
				$("#cert_uploadbutton").off('click').on('click', function() {
					if ( up_file.length == 0 ) {
						return;
					}
					data.submit();
					up_file = null;
				});
			},
			//
	        done: function (e, data) {
	        	//
				//$('#progress .progress-bar').css('width', 0 + '%');        	
	        	//
	            var response = data.result[0];
	            var msg = response.result;
				if ( msg == "yes" ) {
		            //$('<p/>').text(msg).appendTo('#files');
		            $("#message").html("<font color=blue>apns 등록성공</font>");
					setTimeout(function() {
						$('#progress1 .bar').css('width', '0%');
						$("#message").html("");
						$('#fileupload1')[0].reset();
					}, 7000);
				} else {
					$("#message").html("<font color=blue>apns 등록실패</font>");
					setTimeout(function() {
						$('#progress1 .bar').css('width', '0%');
						$("#message").html("");
						$('#fileupload1')[0].reset();
					}, 3000);
				}
	        },
	    	//
			progress : function(e, data) {
					//alert(data.loaded +' -> ' + data.total);
					var progress = parseInt(data.loaded / data.total * 100, 10);
					//$('#progress .bar').css('width', progress + '%');
					$('#progress1 .bar').css('width', progress + '%');
				}
		}); // fileupload
		
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
		<form id="fileupload1" action="UploadServlet" method="POST" enctype="multipart/form-data">
		<input type="hidden" id="user_id" name="user_id" value="<%=strUserId%>">
		<fieldset class="myfields">
			<legend>APPLE PUSH 정보 등록</legend>
			<div>
				<label class="sandbox"><input type="radio" name="sandbox" value="N"/><span>배포용</span></label>
				<label class="sandbox"><input type="radio" name="sandbox" value="Y"/><span>개발용</span></label>
			</div>
			<label for="push_name">Push App Name: </label><input type="text" id="push_name" name="push_name" />
			<label for="app_id">Apple App ID: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><input type="text" id="app_id" name="app_id" />
			<label for="cert_pwd">인증서 비밀번호: </label><input type="password" id="cert_pwd" name="cert_pwd" />
			<!-- start jquery upload -->
			<div>
			<p class="va_upload">인증서 파일:
	        	<input type="button" id="file_btn_cert" class="sel_btn" value="파일선택"> 
	        	<input type="file" id="file_param_1" class="file_btn" name="files[]">
	        	<input type="text" id="file_name_1" class="file_name">
	        </p> 
			<p class="va_upload"><input type="button" id="cert_uploadbutton"  class="exec_btn" value="APNS 정보 등록" /></p>
			</div>
			<div id="progress1" class="progress">
				<div class="bar" style="width: 0%;"></div>
			</div>
			<!-- end jquery upload -->
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