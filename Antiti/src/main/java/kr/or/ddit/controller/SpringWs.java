package kr.or.ddit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import kr.or.ddit.service.ChatService;
import kr.or.ddit.vo.ChatMessageVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.WsSessionVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringWs extends TextWebSocketHandler {
	private static List<WsSessionVO> userSessionList = 
			Collections.synchronizedList(new ArrayList<WsSessionVO>());
	
	@Autowired
	ChatService chatService;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("누군가 접속");
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Iterator<WsSessionVO> wsSessionIt = userSessionList.iterator();
		while(wsSessionIt.hasNext()) {
			WsSessionVO wsVO = wsSessionIt.next();
			if(wsVO.getWebSocketSession() == session) {
				userSessionList.remove(wsVO);
				log.info(wsVO.getName() + "접속 종료...");
				break;
			}
		}
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload();
		log.info(msg);
		
		// 받은 메시지를 풀어서 ChatMessageVO 객체에 저장
		// command가 message일 때를 제외한 모든 chatMessage는 유저명
		Gson gson = new Gson();
		ChatMessageVO chatMessageVo = gson.fromJson(msg, ChatMessageVO.class);
		
		// command : connect, open, close, message
		// chatMessage : username        / message
		String command = chatMessageVo.getCommand();
		String chatMessage = chatMessageVo.getMessage();
		
		// 첫 연결일때
		if("connect".equals(command)) {
			// 연결이 되면 유저 세션, 이름, 채팅창 상태(0: 꺼짐 / 1: 켜짐) 세팅
			userSessionList.add(new WsSessionVO(session, chatMessage, 0));
			
			// 전체 채팅 리스트를 가져와서 true, 채팅내용, 보낸사람, 안읽은 사람수를
			// 반복문을 통해서 하나씩 jsp로 전송
			List<ChatVO> chatList = chatService.getChatList();
			for(ChatVO chatVO : chatList) {
				session.sendMessage(buildJsonTextMessage("true", chatVO.getChatCont(), chatVO.getStuNum(), chatVO.getReadCount()));
			}
			
			// 처음 연결했을때 쌓여있는 메시지 알림 수 전송
			int count = chatService.getMsgCount(chatMessage);
			session.sendMessage(buildJsonTextMessage("true", count + "", "countMsg", -1));
		}
		
		// 채팅창을 열었을 때
		if("open".equals(command)) {
			for(WsSessionVO wsVO : userSessionList) {
				if(wsVO.getWebSocketSession() == session) {
					wsVO.setStatus(1);
					ChatVO chatVO = new ChatVO();
					// 지금은 이름=stuNum이지만 원래 stuNum은 학번
					chatVO.setStuNum(wsVO.getName());
					chatService.updateLast(chatVO);
					break;
				}
			}
			// 누가 채팅창을 열어서 읽으면 refresh
			List<ChatVO> chatList = chatService.getChatList();
			for(WsSessionVO wsVO : userSessionList) {
				wsVO.getWebSocketSession().sendMessage(buildJsonTextMessage("refresh", "", "", -1));
				for(ChatVO chatVO : chatList) {
					wsVO.getWebSocketSession().sendMessage(buildJsonTextMessage("true", chatVO.getChatCont(), chatVO.getStuNum(), chatVO.getReadCount()));
				}
			}
		}
		
		// 채팅창을 닫았을 때
		if("close".equals(command)) {
			for(WsSessionVO wsVO : userSessionList) {
				if(wsVO.getWebSocketSession() == session) {
					wsVO.setStatus(0);
				}
			}
		}
		
		// 메시지가 오면
		if("message".equals(command)) {
			ChatVO chatVO = new ChatVO();
			WsSessionVO wsVO = null;
			int cnt = 0;
			
			for(WsSessionVO sessionVO : userSessionList) {
				// 보낸사람 vo 찾기
				if(sessionVO.getWebSocketSession() == session) {
					wsVO = sessionVO;
					break;
				}
			}
			// 찾은 vo에서 이름을 가져와서 CHAT테이블에 insert
			// 지금은 이름=stuNum이지만 원래 stuNum은 학번
			chatVO.setStuNum(wsVO.getName());
			chatVO.setChatCont(chatMessage);
			chatService.insertChat(chatVO);
			
			for(WsSessionVO sessionVO : userSessionList) {
				// 채팅창이 열려있는 유저의 읽은 채팅 카운트 update
				if(sessionVO.getStatus() == 1) {
					ChatVO chatName = new ChatVO();
					// 지금은 이름=stuNum이지만 원래 stuNum은 학번
					chatName.setStuNum(sessionVO.getName());
					chatService.updateLast(chatName);
				}
				// 채팅창이 닫혀있는 사람 카운트
				if(sessionVO.getStatus() == 0) cnt ++;
			}
			
			int stuCount = chatService.getStuCount();
			cnt += stuCount - userSessionList.size();
			
			sendToAll(buildJsonTextMessage("false", chatMessage, wsVO.getName(), cnt));
		}
		
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	}
	
	private void sendToAll(TextMessage message) throws IOException {
		for(WsSessionVO wsVO : userSessionList) {
			wsVO.getWebSocketSession().sendMessage(message);
		}
	}
	
	private TextMessage buildJsonTextMessage(String isConn, String chatMessage, String name, int readCount) {
		Gson gson = new Gson();
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		if(chatMessage != null) {
			jsonMap.put("isConn", isConn);
			jsonMap.put("message", chatMessage);
			jsonMap.put("name" , name);
		}
		if(readCount != -1) {
			jsonMap.put("readCount", readCount + "");
		}
		
		String strJson = gson.toJson(jsonMap);
		log.info("strJson : " + strJson);
		
		return new TextMessage(strJson);
	}
	
}
