package kr.or.ddit.vo;

import java.util.Date;

import lombok.Data;

@Data
public class ChatVO {
	private int chatNum;
	private String stuNum;
	private String chatCont;
	private Date chatDate;
}
