package kr.or.ddit.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Login {
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam String id, Model model, HttpServletRequest req) {
		HttpSession session = req.getSession();
		session.setAttribute("id", id);
		
		return "wsMain";
	}
}
