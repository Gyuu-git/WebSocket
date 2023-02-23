package kr.or.ddit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.mapper.ChatMapper;
import kr.or.ddit.service.ChatService;
import kr.or.ddit.vo.ChatVO;

@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	ChatMapper chatMapper;

	@Override
	public List<ChatVO> getChatList() {
		return chatMapper.getChatList();
	}

	@Override
	public void insertChat(ChatVO chatVO) {
		chatMapper.insertChat(chatVO);
	}

	@Override
	public void updateLast(ChatVO chatVO) {
		chatMapper.updateLast(chatVO);
	}

	@Override
	public int getMsgCount(String chatMessage) {
		return chatMapper.getMsgCount(chatMessage);
	}
	
}
