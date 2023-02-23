package kr.or.ddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/tug")
@Slf4j
@Controller
public class ToastUIGrid {

	@GetMapping("/home")
	public String home() {
		log.info("tug홈");
		return "tug/home";
	}
}
