package kr.or.ddit.mapper;

import java.util.List;

import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.ReadCountVO;

public interface ChatMapper {
	public List<ChatVO> getChatList();

	public void insertChat(ChatVO chatVO);

	public void updateLast(ChatVO chatVO);

	public int getMsgCount(String chatMessage);

	public List<ReadCountVO> readCount();

	public int getStuCount();
}
