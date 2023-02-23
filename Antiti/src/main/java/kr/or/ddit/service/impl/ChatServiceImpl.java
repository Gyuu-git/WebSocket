package kr.or.ddit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.mapper.ChatMapper;
import kr.or.ddit.service.ChatService;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.ReadCountVO;

@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	ChatMapper chatMapper;

	@Override
	public List<ChatVO> getChatList() {
		List<ChatVO> chatVOList = chatMapper.getChatList();
		List<ReadCountVO> rcList = chatMapper.readCount();
		
		for(ReadCountVO rcVO : rcList) {
			for(ChatVO chatVO : chatVOList) {
				if(chatVO.getChatNum() > rcVO.getLastChat()) {
					chatVO.setReadCount(chatVO.getReadCount() + rcVO.getPlus());
				}
			}
		}
		return chatVOList;
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

	@Override
	public int getStuCount() {
		return chatMapper.getStuCount();
	}
	
}
