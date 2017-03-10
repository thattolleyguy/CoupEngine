package com.ttolley.coup.resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoupModelController {

	@RequestMapping("/coup/gui")
	public String landing(Model model) {
		return "landing";
	}
	
	@RequestMapping("/coup/gui/result") 
	public String result(Model model) {
		return "result";
	}
}
