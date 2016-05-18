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

<%! List<DeviceIdInfo> getPushGroupDeviceListEx(String strGroupId) {
	
	List<DeviceIdInfo> resList = null;
	//
	int nStartPos = 0;
	int nPageSize = 50;
	//
	String strError = null;
	SqlSession sqlSession = null;

	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		resList = userMapper.getPushGroupDeviceListEx(strGroupId);

	} catch (Exception e) {
		e.printStackTrace();

	} finally {
		sqlSession.close();
	}

	return resList;
}%>

<%
	String push_user_seq = request.getParameter("push_user_seq");
	String push_name = request.getParameter("push_name");
	String group_id = request.getParameter("group_id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css" />
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<title>push 기기 목록</title>
<script type="text/javascript">
	$(document).ready( function() {
		//
		$("#btn_delete_device").click( function() {
			//
			var group_id = "<%=group_id%>";
			//
			var arr_seq = new Array();
			var nCnt = $("#device_count").val();
			for ( i = 0; i < nCnt; i++ ) {
				var chk_id = "chk_" + i;
				var chk_val = $("#" + chk_id).val();
				//alert(chk_val);
				if ( $('input:checkbox[id="' + chk_id +  '"]').is(":checked") == true ) {
					//alert("[" + i + "]");
					arr_seq.push(chk_val);
				}
			}
			//			
			if ( arr_seq.length == 0 ) {
				$("#message").html("<font color=blue>선택된 값이 없습니다.</font>");
				setTimeout(function() { 
					$("#message").html("");
				}, 3000);
				return false;
			}
			//alert(arr_seq);
			
			//
			var action = "edit_group_delete_device_array.jsp";
			var strArrSeq = JSON.stringify(arr_seq);
			var form_data = {
				group_id: group_id,
				seq_list: strArrSeq,
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
						window.opener.location.reload();
						window.close();
						// refresh parent windows needed...
						
					} else {
						//alert(response);
						$("#message").html("<font color=blue>그룹에서 기기삭제가 실패했습니다.</font>");
						setTimeout(function() {
							$("#message").html("");
						}, 3000);
					}
				} // function
			}); // $.ajax
			return false;
		}); // delete_btn click
	}); // ready(function()
</script>
</head>
<body>
push 기기 목록<br>
<!-- 
hi_tarot 의 review_list.jsp 참고해서 제작하기.
개발의 편의성을 위해서 네비게이션 부분은 제외한다.
 -->
<ul>
<li>사용자: <%= strUserId %></li>
<p>
<li>push 이름: <%= push_name %></li>
<p>
<button id="btn_delete_device">선택된 기기 삭제</button>
<p>
<p id="message"></p>
<li>push 기기 목록</li>
<ul>
<%
	int nDeviceCount = 0;
	//List<DeviceIdInfo> liResult = getPushGroupDeviceListEx(group_id);
	List<DeviceIdInfo> resList = getPushGroupDeviceListEx(group_id);
	for ( int i = 0; i < resList.size(); i++ ) {
		DeviceIdInfo info = resList.get(i);
		String[] arrItem = info.device_id.split("\\|");
		if ( arrItem.length != 3 )
			continue;
		nDeviceCount++;
		String strChkId = String.format("chk_%d", i);
		String strChk = String.format("<input type=\"checkbox\" id=\"%s\" name=\"%s\" value=\"%d\" />",
				strChkId, strChkId, info.seq);
		
		String strText = String.format("(%s) %s", 
				arrItem[0], arrItem[2]); 
		out.println("<li>" + strChk + "&nbsp"+ strText + "</li>");
	}
	//
	if ( false ) {
		for ( int i = 0; i < 100; i++ ) {
			String strText = String.format("(%d) %d", i, i); 
			out.println("<li>" + strText + "</li>");
		}
	}
%>
</ul>
</ul>
<form id="form1">
<input type="hidden" id="device_count" value=<%=nDeviceCount %>>
</form>
</body>
</html>