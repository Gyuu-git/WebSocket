<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/ws/resources/css/withmeChat.css">
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="/ws/resources/js/jquery-3.6.1.min.js"></script>
<script type="text/javascript" src="/ws/resources/js/jquery-ui.min.js"></script>
<title>도서관리시스템</title>
<style type="text/css">
#chatTopBar{
	background: black;
}
#countMsg{
	width: auto;
	height: auto;
	color: white;
	background: red;
	border-radius: 100px;
	text-align: right;
	position: absolute;
	left: 50px;
	top: -25px;
	z-index: 1043;
}
</style>
</head>
<body>
<% String id = (String) session.getAttribute("id"); %>
<h3><%=id %></h3>

<!-- <div id="chat"> -->
<!-- 	<div id="chatList" ></div> -->
<!-- 	<input type="text" id="sendText" /> -->
<!-- 	<button id="send" onclick="sendMessage()" >전송</button> -->
<!-- </div> -->
<!-- <div> -->
<!-- 	<input type="button" id="chatButton" value="open" /> -->
<!-- 	<div id="countMsg"></div> -->
<!-- </div> -->
<!-- 채팅 아이콘 버튼 -->

<div class="openChat chatBox" id="openChat">
	<img class="chat openChat" src="/ws/resources/images/off.png">
	<p id="countMsg"></p>
</div>
<div id="main-container">
	<div id="chatTopBar">
		<button id="closeChat">X</button>
	</div>
	<div id="chat-container"></div>
	<div id="bottom-container">
	
		<!-- 송신 메시지 텍스트박스 -->
		<input id="messageText" type="text">
		
		<!-- 송신 버튼 -->
		<input type="button" value="전송" id="btn_sendText" onclick="sendMessage()">
	</div>
</div>
<script type="text/javascript">
// 	var webSocket = new WebSocket("ws://192.168.145.10/WebSocketEx/socket");

$(function() {
	// 소켓 초기화
	webSocket = new SockJS("/ws/socket");
	
	// 웹소켓 연결
	webSocket.onopen = function onOpen(event) {
		webSocket.send(createMessage("connect", "<%=id %>"));
	}

	// 메시지가 오면
	webSocket.onmessage = function onMessage(message) {
		var jsonData = JSON.parse(message.data);
		console.log(jsonData);
		
		var name = jsonData.name;
		var message = jsonData.message;
		var isConn = jsonData.isConn;
		var readCount = jsonData.readCount;
		
		// 메시지 추가
		if(name == "countMsg"){
			if(message != '0'){
				$("#countMsg").text(message);
			}
			return;
		}
		
		var chat = "";
		if(name == "<%=id %>"){
			chat = "<div class='my-chat-box'>"
			if(readCount != 0){
				chat += "<span class='chat readCount'>" + readCount + "</span>";
			}
			chat += "<span class='chat my-chat'>" + message + "</span></div><br>";
		}else{
			chat = "<div class='chat-box'><div>" + name + "</div>";
			if(readCount != 0){
				chat += "<span class='chat'>" + message + "</span>";
			}
			chat += "<span class='chat readCount'>" + readCount + "</span></div><br>";
			
			if(isConn == 'false'){
				var status = $("#main-container").css("display");
				if(status != "none"){
				}else{	// 꺼진 상태라면 알림 +1
					var count = $("#countMsg").text();
					if(count == "") count = 0;
					count = parseInt(count) + 1;
					$("#countMsg").text(count);
					console.log($("#countMsg").text());
				}
			}
		}
		console.log(chat);
		$("#chat-container").append(chat);
		$('#chat-container').scrollTop($('#chat-container')[0].scrollHeight+100);
	}

	webSocket.onerror = function onError(event) {
		alert("오류 : " + event.data);
	}
	
	// 채팅창 켜기
	$("#openChat").on("click", function() {
		var rcArr = $(".readCount");
		var alerm = $("#countMsg").text();
		for(var i = rcArr.length -1; i > alerm; i--){
			console.log(i + " : " + rcArr[i].innerText);
			rcArr[i].innerText = rcArr[i].innerText - 1;
		}
		
		$("#openChat").css("display", "none");
		$("#main-container").css("display", "block");
		$("#countMsg").text("");
		webSocket.send(createMessage("opne", "<%=id %>"));
	});
	
	// 채팅창 끄기
	$("#closeChat").on("click", function() {
		$("#openChat").css("display", "block");
		$("#main-container").css("display", "none");
		webSocket.send(createMessage("close", "<%=id %>"));
	});
	
	// 엔터키 입력 시 채팅 전송
	$('#messageText').keydown(function(key){
		if(key.keyCode == 13){
			$('#messageText').focus();
			sendMessage();
		}
	});

	$( "#main-container" ).draggable({ handle: "#chatTopBar" });
});

// 메시지 전송버튼 클릭 시
function sendMessage() {
	if($("#messageText").val().trim()==""){
		$("#messageText").focus();
		return;
	}
	
	webSocket.send(createMessage("message", $("#messageText").val()));
	$("#messageText").val("");
	
}

// 보낼 메시지 json방식으로 만듦
function createMessage(command, message) {
	return '{"command" : "' + command + '", "message" : "' + message + '"}';
}

function closing(){
	webSocket.close();
}

window.onbeforeunload = function(){
	closing();
}
</script>
</body>
</html>