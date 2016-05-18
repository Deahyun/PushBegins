<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
//	String strUserId;
	if ( session.getAttribute("push_user_id") == null ) {
		//strUserId = null;
		String strGotoPage = "login.html";
%>
<script type="text/javascript">
	location.href="<%= strGotoPage %>"; 
</script>
<%
		return;
	}
	
	String strUserId = session.getAttribute("push_user_id").toString();
	String strUserSeq = session.getAttribute("push_user_seq").toString();
%>
