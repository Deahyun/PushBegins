<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Apple Push 등록</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="css/register.css" media="all" />
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
	            var msg = response.name + ' -> upload done';
	            //$('<p/>').text(msg).appendTo('#files');
	            $("#message").html("<font color=blue>" + msg + "</font>");
				setTimeout(function() {
					$('#progress1 .bar').css('width', '0%');
					$("#message").html("");
					//$('#fileupload1')[0].reset();
				}, 3000);
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
	<form id="fileupload1" action="UploadServlet" method="POST" enctype="multipart/form-data">
		<input type="hidden" id="email" value="<%=strUserId%>">
		<fieldset>
			<legend>APPLE PUSH 정보 등록</legend>
			<label for="push_name">Push App Name: </label><input type="text" id="push_name" />
			<label for="cert_pwd">인증서 비밀번호: </label><input type="password" id="cert_pwd" />
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
</body>
</html>