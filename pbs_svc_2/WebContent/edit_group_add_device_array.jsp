<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="java.util.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Reader"%>
<%@page import="com.site.config.*"%>
<%@page import="com.site.dao.*"%>
<%@page import="com.site.entity.*"%>
<%@page import="com.site.sqlmap.*"%>
<%@ page import="org.apache.ibatis.session.*" %>

<%@page import="org.json.simple.*"%>

<%@ page import="com.entertera.info.*"  %>
<%@ page import="utils.*"  %>

<%
	String strError = "parameter not found";
	String tag = "edit_group_add_device_array";

	String strGroupId = null;
	List<Integer> liSeq = new ArrayList<Integer>();
	try {
		strGroupId = request.getParameter("group_id");
		String strResult = request.getParameter("seq_list");
		//
		//UserLog.Log(tag, strResult);
		//
		JSONArray itemList = (JSONArray)JSONValue.parse(strResult);
		//
		for ( int i = 0; i < itemList.size(); i++ ) {
			String strSeq = itemList.get(i).toString();
			int nSeq = Integer.parseInt(strSeq);
			liSeq.add(nSeq);
  		}
		
	} catch ( Exception e ) {
		out.println(e.getMessage());
		out.flush();
		return;
	}

	if ( false ) {
		for ( int i = 0; i < liSeq.size(); i++ ) {
			UserLog.Log(tag, "seq -> " + liSeq.get(i));
		}
	}
	
	boolean bResult = false;
	SqlSession sqlSession = null;
	
	try {
		sqlSession = ConnectionFactory.getSession().openSession();
		//
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
		for ( int i = 0; i < liSeq.size(); i++ ) {
			int nDeviceSeq = liSeq.get(i);
			userMapper.addDeviceSeqToGroup(strGroupId, nDeviceSeq);
		}
		// 중복된 device_seq 를 제거한다.
		userMapper.removeDuplicateSeqFromGroup(strGroupId);
		//
		sqlSession.commit();
		//
		bResult = true;
		
	} catch ( Exception e ) {
		sqlSession.rollback();
		strError = e.getMessage();
		UserLog.Log(tag, "error -> " + strError);

	} finally {
		sqlSession.close();
	}
	
	if ( bResult ) {
		out.print("ok");
		
	} else {
		out.print("failure");
	}

	out.flush();
%>