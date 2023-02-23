package kr.or.ddit.mapper;

import java.util.List;

import kr.or.ddit.vo.ChatVO;

public interface ChatMapper {
	public List<ChatVO> getChatList();

	public void insertChat(ChatVO chatVO);

	public void updateLast(ChatVO chatVO);

	public int getMsgCount(String chatMessage);
}
