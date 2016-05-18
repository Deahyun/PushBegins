<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>PushBegins 사용자별 기본메뉴</title>
</head>
<body>
<h3>PushBegins 기본메뉴</h3><br>
<ul>
<li>사용자: <%= strUserId %></li>
<p>

<li>app 의 push 관련 정보 등록</li>
<ul>
	<li><a href="register_gcm.jsp">google push(GCM) 정보등록</a></li>
	<li><a href="register_apns_develop.jsp">apple push(APNS) 정보등록</a></li>
</ul>
<p>

<li>app 의 그룹 관리</li>
<ul>
	<li><a href="gcm_push_group_list.jsp?user_seq=<%=strUserSeq%>">android push 앱 그룹 관리</a></li>
	<li><a href="apns_push_group_list.jsp?user_seq=<%=strUserSeq%>">ios push 앱 그룹 관리</a></li>
	<!--
	<li><a href="apns_push_group_list.jsp?user_seq=<%=strUserSeq%>">통합 push 앱 그룹 관리</a></li>
	-->
</ul>
<p>

<li>push 메시지 보내기</li>
<ul>
	<li><a href="gcm_push_list.jsp?user_seq=<%=strUserSeq%>">google push 보내기</a></li>
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">apple push 보내기</a></li>
	<!--
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">통합 push 선택(push-name 으로 일괄전송)</a></li>
	-->
</ul>
<p>

<li>보낸 push 메시지 목록보기</li>
<ul>
	<!--
	<li><a href="gcm_push_list.jsp?user_seq=<%=strUserSeq%>">google push 메시지 목록</a></li>
	<li><a href="apns_push_list.jsp?user_seq=<%=strUserSeq%>">apple push 메시지 목록</a></li>
	-->
	<li><a href="push_message_list.jsp?user_seq=<%=strUserSeq%>">전체 push 메시지 목록</a></li>
</ul>
<p>

<li>사용자 피드백(feedback) 메시지보기 </li>
<ul>
	<li><a href="feedback_message_list.jsp?user_seq=<%=strUserSeq%>">피드백(feedback) 메시지 목록</a></li>
</ul>
<p>

<li>API 방식으로 push 보내는 방법</li>
<ul>
	<li>GCM 과 APNS 를 동일한 방식으로 보낸다.</li>
	<li>push 전송을 위해 접속하는 url 은 http(s)://사용자_url:사용자_port/pbs_svc/Listener 이 된다.</li>
	<li></li>
	<li>아래는 push 사용자가 teraget@gmail.com 이고 등록된 push 이름이 pushbegins 인  안드로이드 기기(proj_num 은 [android push 앱 그룹 관리] 에서 참고)에 push 를 보내는 json API 예제이다.</li>
	<li>'{"io_kind":"send_push_req", "user_id":"teraget@gmail.com", "push_name":"pushbegins", \
"device_id_ex":"a|515463805231|01012345678'", \
"json_data":{"alert":"This is title", "user_data":"This is long message"}}'</li>
	<li></li>
	<li>아래는 push 사용자가 teraget@gmail.com 이고 등록된 push 이름이 pushbegins-develop 인 아이폰 기기(app_id 는 [ios push 앱 그룹 관리] 에서 참고)에 push 를 보내는 json API 예제이다.</li>
	<li>'{"io_kind":"send_push_req", "user_id":"teraget@gmail.com", "push_name":"pushbegins-develop", \
"device_id_ex":"i|com.entertera.pushbegins|01012345678'", \
"json_data":{"alert":"This is title", "user_data":"This is long message"}}'</li>
</ul>
<p>

</body>
</html>