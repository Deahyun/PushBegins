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

<%!
String tag =  "push_group_list";
List<RegisterGcmInfo> getGcmPushList(String strUserSeq) {
	
	List<RegisterGcmInfo> resList = null;
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getGcmPushList(nUserSeq);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return resList;
}

List<String> getPushGroupList(String strUserSeq) {
	//
	List<String> resList = null;
	//
	int nUserSeq = 0;
	try {
		nUserSeq = Integer.parseInt(strUserSeq);
		
	} catch ( Exception e ) {
		//
		UserLog.Log(tag, "no result found");
		return resList;
	}
	
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		resList = userMapper.getPushGroupList(nUserSeq);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return resList;
}

List<String> getAppList(String strUserSeq) {
	
	List<String> resList = null;
	//
	int nStartPos = 0;
	int nPageSize = 50;
	//
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		int nUserSeq = Integer.parseInt(strUserSeq);
		resList = userMapper.getAppList(nUserSeq, nStartPos, nPageSize);

	} catch (Exception e) {
		e.printStackTrace();

	} finally {
		sqlSession.close();
	}

	return resList;
}

List<DeviceIdInfo> getPushGroupDeviceListEx(String strGroupId) {
	//
	List<DeviceIdInfo> resList = null;
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		resList = userMapper.getPushGroupDeviceListEx(strGroupId);

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}

	return resList;
}

void deleteGroup(String strGroupId) {
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		userMapper.deleteGroup(strGroupId);
		sqlSession.commit();

	} catch (Exception e) {
		sqlSession.rollback();
		strError = e.getMessage();
		System.out.println(strError);

	} finally {
		sqlSession.close();
	}
}

%>

<%
	String push_user_seq = request.getParameter("push_user_seq");
	String push_name = request.getParameter("push_name");
	String group_id = request.getParameter("group_id");
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
	//
	var w = 640;
	var h = 480;
	//	
	LeftPosition=(screen.width-w)/2;
	TopPosition=(screen.height-h)/2;
	//
	function popup_open(popup_case, push_user_seq, push_name, group_id) {
		var popup_url = "index.html";
		if ( popup_case == 1 ) {
			// add device
			popup_url = "edit_group_add_device.jsp";
			
		} else if ( popup_case == 2 ) {
			// delete device
			popup_url = "edit_group_delete_device.jsp";

		} else {
			// extension...
			return;
		}
		
	    window.open(
			popup_url+"?push_user_seq=" + push_user_seq + "&push_name=" + push_name + "&group_id=" + group_id, 
			"popup",
	        "width="+w+",height="+h+",top="+TopPosition+",left="+LeftPosition+",location=no");
	} // function
	
	</script>
	<script type="text/javascript">
$(document).ready( function() {
	//	
	$("#btn1").click( function() {
		//$("p").append("<b> append some text</b>");
		popup_open(1, "<%=push_user_seq%>", "<%=push_name%>", "<%=group_id%>");
	});
	$("#btn2").click( function() {
		//$("ol").append("<li>append some item</li>");
		//$("#list1").append("<li>append some item</li>");
		popup_open(2, "<%=push_user_seq%>", "<%=push_name%>", "<%=group_id%>");
	});
	$("#btn4").click( function() {
		var group_id = "<%=group_id%>";
		var push_name = "<%=push_name%>";
		window.location.href = "113_send_group_push.jsp?group_id=" + group_id + "&push_name=" + push_name;
	});
	
	//
	$("#btn3").click( function() {
		//
		var action = "edit_group_delete_group_req.jsp";
		var group_id = "<%=group_id%>";
		var form_data = {
			group_id: group_id,
			is_ajax: 1
		}; // var
		//
		$.ajax({
			type: "POST",
			url: action,
			data: form_data, 
			success: function(response) {
				//alert("response -> " + response);
				if ( response == 'ok' ) {
					//alert("response == ok");
					var user_seq = "<%=strUserSeq%>";
					//window.location.href = "push_group_list.jsp?user_seq="+user_seq;
					window.location.href = "110_manage_group.jsp?user_seq="+user_seq;
					
				} else {
					//alert(response);
					$("#message").html("<font color=blue>그룹삭제가 실패했습니다.</font>");
					setTimeout(function() {
						$("#message").html("");
					}, 3000);
				}
			} // function
		}); // $.ajax
		return false;
	});
	//
	
	
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
		<ul>
			<h2><font color="green">push group 관리</font></h2><p>
		</ul>
		<ul>
			<p id="message"></p>
		</ul>
		
		<ul>
		<li>사용자: <%= strUserId %></li>
		<p>
		<button id="btn1">기기 추가</button>
		<button id="btn2">기기 삭제</button>
		<button id="btn3">[<%= group_id %>] 삭제</button>
		<button id="btn4">group push 보내기</button>
		<p>
		
		<li>[<%= group_id %>] 에 소속된 기기 ID</li>
		<ul id="list1">
		<%
			List<DeviceIdInfo> liResult = getPushGroupDeviceListEx(group_id);
			for ( int i = 0; i < liResult.size(); i++ ) {
				DeviceIdInfo info = liResult.get(i);
				String[] arrItem = info.device_id.split("\\|");
				if ( arrItem.length != 3 )
					continue;
				
				String strText = String.format("<li>(%s) %s</li>", arrItem[0], arrItem[2]);
				out.println(strText);
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