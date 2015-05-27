package com.ptaas.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ptaas.service.HomeSvc;

@Controller
public class HomeController {
	
	@Autowired
	private HomeSvc homeSvc;
	
	@RequestMapping("/")
	public String root(Principal principal) {
		
		homeSvc.saveUserDetails(principal);
		return "home";
	}
	
	@RequestMapping("/home")
	public String home(Principal principal) {
		homeSvc.saveUserDetails(principal);
		return "home";
	}
	
}
