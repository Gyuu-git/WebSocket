package kr.or.ddit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@ServerEndpoint("/wsMain.do")
public class WsMain {
	private static List<Session> userSessionList = 
			Collections.synchronizedList(new ArrayList<Session>());
	
	private static Map<String, Integer> subjects = new HashMap<>();
	
	@OnOpen
	public void onOpen(Session userSession) throws IOException {
		if(userSessionList.size() == 0) {
			subjects.put("과목1", 0);
			subjects.put("과목2", 4);
			subjects.put("과목3", 2);
		}
		userSessionList.add(userSession);
		log.info(userSession.getId() + "연결...");
		
		sendToAll();
	}
	
	@OnMessage
	public void onMessage(Session userSession) {
		
	}
	
	@OnClose
	public void onClose(Session userSession) {
		log.info(userSession.getId() + "접속 종료...");
		for(Session us : userSessionList) {
			if(us.equals(userSession)) {
				userSessionList.remove(us);
			}
		}
	}
	
	@OnError
	public void onError(Throwable t){
        t.printStackTrace();
    }

	private void sendToAll() throws IOException {
		Gson gson = new Gson();
		String subJson = gson.toJson(subjects);
		
		for(Session us : userSessionList) {
			us.getBasicRemote().sendText(subJson);
		}
	}
}
