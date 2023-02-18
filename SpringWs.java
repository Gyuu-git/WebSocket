package kr.or.ddit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringWs extends TextWebSocketHandler {
	private static List<WebSocketSession> userSessionList = 
			Collections.synchronizedList(new ArrayList<WebSocketSession>());
	
	private static Map<String, Integer> subjects = new HashMap<>();
	{
		subjects.put("과목1", 0);
		subjects.put("과목2", 4);
		subjects.put("과목3", 2);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		userSessionList.add(session);
		System.out.println(session.getId() + "연결...");
		
		sendToAll(new TextMessage(""));
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info(session.getId() + "접속 종료...");
		for(WebSocketSession us : userSessionList) {
			if(us.equals(session)) {
				userSessionList.remove(us);
			}
		}
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.info("ms : " + message.getPayload());
		String[] com = message.getPayload().split(",");
		if(com[1].equals("insert")) {
			subjects.put(com[0], subjects.get(com[0]) + 1);
		}
		sendToAll(new TextMessage(""));
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	}
	
	private void sendToAll(TextMessage message) throws IOException {
		List<TextMessage> tmList = new ArrayList<>();
		Gson gson = new Gson();
		tmList.add(new TextMessage(gson.toJson(subjects)));
		if(!message.getPayload().equals("")) {
			tmList.add(new TextMessage(gson.toJson(message.getPayload())));
		}
		
		for(WebSocketSession us : userSessionList) {
			for(TextMessage tm : tmList) {
				us.sendMessage(tm);
				log.info("tm : " + tm);
			}
		}
	}
}
