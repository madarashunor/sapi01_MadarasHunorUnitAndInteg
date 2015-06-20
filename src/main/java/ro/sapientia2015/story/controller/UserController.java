package ro.sapientia2015.story.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.sapientia2015.story.dto.SprintDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.UserDTO;
import ro.sapientia2015.story.model.Sprint;
import ro.sapientia2015.story.model.User;
import ro.sapientia2015.story.service.SprintService;
import ro.sapientia2015.story.service.UserService;

@Controller
public class UserController {

	@Resource
	private UserService service;
	
	public static final String VIEW_LIST = "user/list";
	public static final String VIEW_ADD = "user/add";

	public static final String MODEL_ATTRIBUTE = "user";

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String listUsers(Model model) {

		List<User> users = service.findAll();
		model.addAttribute("users", users);
		return VIEW_LIST;
	}
	
    private String createRedirectViewPath(String requestMapping) {
        StringBuilder redirectViewPath = new StringBuilder();
        redirectViewPath.append("redirect:");
        redirectViewPath.append(requestMapping);
        return redirectViewPath.toString();
    }
    
	@RequestMapping(value = "/user/add", method = RequestMethod.GET)
	public String showForm(Model model) {

		UserDTO users = new UserDTO();
		model.addAttribute("user", users);
		return VIEW_ADD;
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute(MODEL_ATTRIBUTE) UserDTO dto, BindingResult result, RedirectAttributes attributes) {
		if(result.hasErrors()){
			return VIEW_ADD;
		}
		
		service.add(dto);
		
		return createRedirectViewPath("/user/list");
	}
	
}
