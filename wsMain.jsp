<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="/ws/resources/js/jquery-3.6.1.min.js"></script>
<title>도서관리시스템</title>
<style>
span{
	margin: 10px;
}
hr{
	width: 30%;
	margin-left : 0;
}
</style>
</head>
<body>
<% String id = (String) session.getAttribute("id"); %>
<h3><%=id %></h3>

<div id="subj"></div>
<div>
	<input type="button" id="open" value="open" onclick="connect()" />
	<input type="button" id="close" value="close" onclick="disconnect()" />
</div>
<script type="text/javascript">
// 	var webSocket = new WebSocket("ws://192.168.145.10/WebSocketEx/socket");
	var webSocket;

$(function() {
	$(document).on("click", ".submit", function() {
		var subject = $(this).prev();
		var sub_name = subject.html().split(" : ")[0];
		
		webSocket.send(sub_name + ",insert");
	});
})
function connect() {
	webSocket = new SockJS("/ws/socket");
	
	webSocket.onopen = onOpen
	webSocket.onmessage = onMessage;
	webSocket.onerror = onError;
	webSocket.onclose = onClose;
}

function disconnect() {
	webSocket.close();
}

function onOpen(event) {
}

function onMessage(message) {
	var html = "";
	$("#subj").html(html);
	var jsonData = JSON.parse(message.data);
	html += "<div><span>과목1 : " + jsonData.과목1 + "</span>"
				+ "<input type='button' class='submit' value='수강신청' /></div>";
	html += "<hr />";
	html += "<div><span>과목2 : " + jsonData.과목2 + "</span>"
				+ "<input type='button' class='submit' value='수강신청' /></div>";
	html += "<hr />";
	html += "<div><span>과목3 : " + jsonData.과목3 + "</span>"
				+ "<input type='button' class='submit' value='수강신청' /></div>";
	$("#subj").append(html);
}

function onClose(event){
	$("#subj").html('');
}

function onError(event) {
	alert("오류 : " + event.data);
}
</script>
</body>
</html>