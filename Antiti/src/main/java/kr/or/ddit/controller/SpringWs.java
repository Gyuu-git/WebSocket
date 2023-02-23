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
		
		String command = chatMessageVo.getCommand();
		String chatMessage = chatMessageVo.getMessage();
		
		// 첫 연결일때
		if("connect".equals(command)) {
			userSessionList.add(new WsSessionVO(session, chatMessage, 0));
			log.info("size : " + userSessionList.size());
			
			List<ChatVO> chatList = chatService.getChatList();
			for(ChatVO chatVO : chatList) {
				session.sendMessage(buildJsonTextMessage("true", chatVO.getChatCont(), chatVO.getStuNum()));
			}
			
			int count = chatService.getMsgCount(chatMessage);
			session.sendMessage(buildJsonTextMessage("true", count + "", "countMsg"));
		}
		
		// 채팅창을 열었을 때
		if("opne".equals(command)) {
			for(WsSessionVO wsVO : userSessionList) {
				if(wsVO.getWebSocketSession() == session) {
					wsVO.setStatus(1);
					ChatVO chatVO = new ChatVO();
					chatVO.setStuNum(wsVO.getName());
					chatService.updateLast(chatVO);
					break;
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
		
		if("message".equals(command)) {
			ChatVO chatVO = new ChatVO();
			WsSessionVO wsVO = null;
			for(WsSessionVO sessionVO : userSessionList) {
				if(sessionVO.getWebSocketSession() == session) {
					wsVO = sessionVO;
					break;
				}
			}
			
			chatVO.setStuNum(wsVO.getName());
			chatVO.setChatCont(chatMessage);
			chatService.insertChat(chatVO);
			
			if(wsVO.getStatus() == 1) {
				chatService.updateLast(chatVO);
			}
			
			sendToAll(buildJsonTextMessage("false", chatMessage, wsVO.getName()));
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
	
	private TextMessage buildJsonTextMessage(String isConn, String chatMessage, String name) {
		Gson gson = new Gson();
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		if(chatMessage != null) {
			jsonMap.put("isConn", isConn);
			jsonMap.put("message", chatMessage);
			jsonMap.put("name" , name);
		}
		
		String strJson = gson.toJson(jsonMap);
		log.info("strJson : " + strJson);
		
		return new TextMessage(strJson);
	}
	
}
