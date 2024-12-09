package com.contact.controller;

import com.contact.helper.Message;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.contact.DAO.UserRepository;
import com.contact.entity.User;

import java.util.Properties;
import java.util.Random;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
//import jakarta.mail.Message;
import jakarta.mail.Transport;

@Controller
public class HomeController {
	Random rand = new Random();
	int OTP1=rand.nextInt(10000);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public ModelAndView home(Model model) {
		model.addAttribute("title", "Home- Smart Contact Manager");
		return new ModelAndView("index");
	}

	@RequestMapping("/about")
	public ModelAndView about(Model model) {
		model.addAttribute("title", "about- Smart Contact Manager");
		return new ModelAndView("about");
	}

	// login without using spring security
	@RequestMapping("/login")
	public ModelAndView login(Model model) {
		model.addAttribute("title", "login- Smart Contact Manager");
		model.addAttribute("user", new User());
		return new ModelAndView("login");
	}

	@RequestMapping("/show")
	public ModelAndView showlogin(@ModelAttribute("name") String name, @ModelAttribute("password") String password) {
		System.out.println(name + " " + password);
		User u1 = userRepository.getByName(name);
		System.out.println(u1);
		if (u1.getPassword().equals(password)) {
			return new ModelAndView("successfullyLogin");
		}
		return new ModelAndView("login");
	}

	
	
	// signup
	@RequestMapping("/SignUp")
	public ModelAndView SignUp(Model model) {
		model.addAttribute("title", "SignUp- Smart Contact Manager");
		model.addAttribute("user", new User());
		return new ModelAndView("signup");
	}
	
	// handler for register user
	@PostMapping("/do_Register")
	public ModelAndView register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		System.out.println(".............." + user);
//remember that BindingResult result should be placed after @ModelAttribute() in any other position it will not work
		try {
			if (!agreement) {
				System.out.println("you have not agreed the terms and conditins");
				throw new Exception("you have not agreed the terms and conditins");
			}

			if (result.hasErrors()) {
				System.out.println("Error!!!" + result.toString());
				model.addAttribute("user", user);
				return new ModelAndView("signup");
			}
//role should be always prefixed with ROLE like "ROLE_USER"
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("USER" + user);

			userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Succesfully Registered!!", "alert-success"));

			return new ModelAndView("signup");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			return new ModelAndView("signup");
		}

	}

	@GetMapping("/signin")
	public ModelAndView customLogin(Model model) {
		model.addAttribute("title", "Login-Page");
		return new ModelAndView("loginSecurity");
	}

	@RequestMapping("/forget")
	public ModelAndView forgetPassword() {
		return new ModelAndView("forgetPassword");
	}

	@PostMapping("/verify")
	String verification(Model model,@ModelAttribute("name") String name,HttpSession session) {
		System.out.println(name);
		session.setAttribute("name", name);
		User u1 = userRepository.getUserByUserName(name);
		if(u1==null) {
			return "Please valid a proper email Id";
		}
		
		String message="hi, "+u1.getName()+" your OTP is:"+OTP1+"";
		String subject="Coders area: confirmations";
		String to=name;
		String from="iamakash7059091452@gmail.com";
		sendEmail(message, subject, to, from);
		return "verification";
	}
	
	@PostMapping("/verify-OTP")
	String verifyOTP(@ModelAttribute("OTP") String OTP) {
	System.out.println("OTP is: "+OTP);
	if(Integer.parseInt(OTP)==(OTP1)) {
		return "success";			
	}
	else return "verification";
	}
	
	@PostMapping("/password-changed")
	String passwordChanging(@ModelAttribute("password") String password, @ModelAttribute("retype") String retype,HttpSession session){
		System.out.println(password+" "+retype);
		System.out.println(session.getAttribute("name"));
		String name=(String)session.getAttribute("name");
		User currentUser=this.userRepository.getUserByUserName(name);
		System.out.println(currentUser);
////		if(passwordEncoder.matches(oldpassword,currentUser.getPassword())){
			currentUser.setPassword(passwordEncoder.encode(password));
			this.userRepository.save(currentUser);
//			session.setAttribute("message", new Message("Password changed successfully","success"));
//			System.out.println("changed successfully");	
//		
		if(password.equals(retype)) {
			return "password-changed";
		}
		else {
			session.setAttribute("message", new Message("Re-entered password should be mathced", "alert-danger"));
			return "success";
		}
	}
	
	private static void sendEmail(String message, String subject, String to, String from) {

			String host="smtp.gmail.com";
			Properties properties=System.getProperties();
			System.out.println("PROPERTIES"+properties);
			
			properties.put("mail.smtp.auth","true");
			properties.put("mail.smtp.starttls.enable","true");
			properties.put("mail.smtp.host",host);
			properties.put("mail.smtp.port","587");
			properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

			String username="iamakash7059091452";
			String password="gepf qukq doqj cedw";
				Session session=Session.getInstance(properties,new Authenticator() {
			
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username,password);
				}
			});
			session.setDebug(true);
			MimeMessage m=new MimeMessage(session);
			
			try {
			m.setFrom(from);			
			m.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);			
			m.setText(message);			
			Transport.send(m);			
			System.out.println("sent success..................");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
