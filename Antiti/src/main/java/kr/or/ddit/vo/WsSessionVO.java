package kr.or.ddit.vo;

import org.springframework.web.socket.WebSocketSession;

import lombok.Data;

@Data
public class WsSessionVO {
	WebSocketSession webSocketSession;
	String name;
	int status;	// 0 : 채팅창 끈 상태 / 1 : 채팅창 킨 상태
	
	public WsSessionVO() {}
	
	public WsSessionVO(WebSocketSession session, String name, int status) {
		this.webSocketSession = session;
		this.name = name;
		this.status = status;
	}
}
