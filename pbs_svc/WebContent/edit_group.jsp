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
	<meta charset="UTF-8">
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<title>Develop Demo Page</title>
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
		window.location.href = "send_group_push.jsp?group_id=" + group_id + "&push_name=" + push_name;
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
					window.location.href = "push_group_list.jsp?user_seq="+user_seq;
					
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
push group 관리<br>
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

</body>
</html>