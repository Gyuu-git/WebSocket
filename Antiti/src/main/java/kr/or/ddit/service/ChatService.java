package kr.or.ddit.service;

import java.util.List;

import kr.or.ddit.vo.ChatVO;

public interface ChatService {
	public List<ChatVO> getChatList();

	public void insertChat(ChatVO chatVO);

	public void updateLast(ChatVO chatVO);

	public int getMsgCount(String chatMessage);

	public int getStuCount();

}
