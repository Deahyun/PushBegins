<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<title>Develop Demo Page</title>
	<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).ready( function() {
	//
	$("#btn1").click( function() {
		$("p").append("<b> append some text</b>");
	});
	$("#btn2").click( function() {
		$("ol").append("<li>append some item</li>");
	});
	$("#btn3").click( function() {
		var idx = $("ol").children().length-1; // get last index
		$("li:eq(" + idx + ")").remove();
	});
	////
}); // ready(function()
</script>
</head>
<body>

<p>First text</p>

<ol>
<li>item 1</li>
<li>item 2</li>
<li>item 3</li>
</ol>

<button id="btn1">append text</button>
<button id="btn2">append list item</button>
<button id="btn3">remove list item</button>

</body>
</html>