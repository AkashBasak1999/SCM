package com.contact.controller;

import java.lang.reflect.Array;


import java.security.Principal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.contact.DAO.UserRepository;
import com.contact.DAO.contactRepository;
import com.contact.entity.Contact;
import com.contact.entity.User;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;

//Spring security
//if a genuine credential is injected then we can login. all those pages that only a logged in user can browse
//we have to protect them. a page can be proteted by its url.
//now there might be multiple urls that we may have to protect to do that we have to make a the url in a partucular pattern
//such as those url consists of 'user' in it can do those tasks

//by using the ("/user") we can make the url prefixed with user
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder passwordencoder;//this encoder is included here for changing the password 
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private contactRepository contRepo;

	// by using this @ModelAttribute as the parameter this particular methode will
	// run every time.
	// so using this, for every individual handler we dont need to make separate
	// handlers to get the model value
	// so what ever model value we set inside this mehtode all handler can access
	// that
	@ModelAttribute
	public ModelAndView addCommonData(Model model, Principal principal) {
		// if we are using spring security then add Principal principal in the argument
		// if we use here the principal spring security the from spring security we have
		// to
		// get credentials the so from the security login page if we want to get the
		// username
		// then we use principal object
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		System.out.println("USERNAME: " + user);
		model.addAttribute("user", user);
		return new ModelAndView("usertemp/user_dashboard");
	}

	// opening add_contact_form
	@RequestMapping("/add-contact")
	public ModelAndView openAddContactForm(Model model) {
		model.addAttribute("title", "Add-Contact");
		model.addAttribute("contact", new Contact());
		return new ModelAndView("usertemp/add_contact_form");
	}

	@RequestMapping("/index")
	public ModelAndView dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User-Dashboard");
		return new ModelAndView("usertemp/user_dashboard");
	}

	// @RequestParam("image") MultipartFile image
	// processing add contact form
	@PostMapping("/contact")
	public String processContact(@ModelAttribute("contact") Contact contact, Principal principal,HttpSession session) {
//		to store the data in the particular user's field we have to fetch that user form the logged in user credential  

		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);
			contact.setUser(user);
			user.getContacts().add(contact);// getContact is a list so in the list we are gonna add this
			userRepo.save(user);
			System.out.println(contact);
			session.setAttribute("message", new Message("Your contact is added successfully", "success"));
		} catch (Exception e) {
			// error message
			session.setAttribute("message", new Message("Something went wrong", "danger"));
			System.out.println(e.getMessage());
		}
		return "usertemp/add_contact_form";
	}
	
//if we want to take some value from the url then we have to keep that value inside curly brace in the url
//number of page is always 0 based so if we put page=0 then we can see page 1
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model,Principal principal) {
////Step 1 to get all the contacts:--->
////		model.addAttribute("title", "Show-Contact");
////		//send the contact list
////		User user=userRepo.getUserByUserName(principal.getName());
////		List<Contact> list=user.getContacts();
////		//in current page we want show 5 result
////		model.addAttribute("list", list);
////		return "usertemp/show_contact";
//		
////step 2 to get all the contacts(with pagination):--->
////if we want to show the contact result with pagination then we have to make another methode that returns in the list format 
		//per page we want to show 5 pages
		

		
		System.out.println("page: "+page);
		model.addAttribute("title", "Show-Contact");
		String name=principal.getName();
		User user=userRepo.getUserByUserName(name);
		System.out.println(user.getName());
		Pageable pageable=PageRequest.of(page,5);
		//PageRequest.of(int pagenumber,int PageSize)
		//pageable consists of the PageNumber and the PageSize
		//PageNumber->page
		
		//if we dont use pagination the we can get it in the form of list 
		//List<Contact> list=userRepo.findContactsByUser(user.getId());
		
		//to get the paginated value use below one 
		Page<Contact> list=userRepo.findContactsByUser(user.getId(),pageable);
		//in the first pagable PageRequest.of(int pagenumber,int PageSize) is (0,5)
		//in the second pagable PageRequest.of(int pagenumber,int PageSize) is (1,5)
		
		model.addAttribute("list",list);
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages",list.getTotalPages());
		System.out.println("PageNumber: "+page);
		System.out.println("Total number of pages: "+list.getTotalPages());		
		return "usertemp/show_contact";
	}
	
	//if only a particular user has given then there conatact only can be shown 
	//any other user's contact can not be seen by any other user
	@GetMapping("/contact_details/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model,Principal principal) {
		System.out.println(cid);
		User user = userRepo.getUserByUserName(principal.getName());
		List<Contact> list = userRepo.findContactsByUser(user.getId());
		Contact contact=userRepo.ContactDetails(cid);
		for (Contact l : list) {
			if (l.getId() == cid) {
				model.addAttribute("details", contact);
				return "usertemp/show_contactDetails";
			}
			System.out.println(l.getId());
		}
		return "usertemp/user-restriction";
	}
	
	@GetMapping("/delete/{cid}")
	public String delete(@PathVariable("cid") Integer cid,HttpSession session,Principal principal) {
		System.out.println(cid);
//we have set the User entity's contacts list as cascadeTypeALL that means it is associated with
//Conatct list. so if we want to delete any of the contact then it will not get deleted becasue it 
//is linked with a particular user.
//so to delete any contact first we have to break that link of it with the user
		
		//1.wrong approach
//		Optional<Contact> contact=contRepo.findById(cid);
//		Contact cont=contact.get();//to get the Contact object from Optional<Contact> we have to us get()
//		cont.setUser(null);//so we are decoupling cont from user
//		contRepo.deleteById(cid);
//		contRepo.save(cont);
//		System.out.println("Deleted");
//		session.setAttribute("message",new Message("Contact deleted Successfully","success"));
//		return "redirect:/user/show-contact/0";

		//2.right approach
		//in the above approach if we are deleting then the data is deleting form the contact table in the website
		//and corresponding userid becomes null but the data is not getting deleted from the database
		//due to cascade type		
		Optional<Contact> contact=contRepo.findById(cid);
		Contact cont=contact.get();//to get the Contact object from Optional<Contact> we have to us get()
		//1.to delete it from the database we have to add orphanremoval=true after cascadetype in User entity (ContactList) 		
		//2.and then we have to delete the particular contact from the list of contact in the User entity		
		cont.setUser(null);//so we are decoupling cont from user
		contRepo.deleteById(cid);
		User user=this.userRepo.getUserByUserName(principal.getName());
		//3.in the User entity we have to override the equal methode in such a way that it will check the 
		//	object of User by its Id
	
		user.getContacts().remove(cont);
		this.userRepo.save(user);
		
		contRepo.save(cont);
		System.out.println("Deleted");
		session.setAttribute("message",new Message("Contact deleted Successfully","success"));
	//when to use redirect?
	//from a current handler if we are calling any page then normal url mapping is there but 	
	//when the current handler is calling a another handler by url then we should use redirect	
		return "redirect:/user/show-contact/0";
		//	System.out.println("horibol");
	}
	
	//contact updating 
	@GetMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model,Principal principal){
		model.addAttribute("title", "Update-Contact");
		Optional<Contact> cont= contRepo.findById(cid);
		Contact contact=cont.get();
		model.addAttribute("contact",contact);
		return "usertemp/update_Contact";
	}
	
	@PostMapping("/process-contact")
	public String processContact1(@ModelAttribute("contact") Contact contact,
		Principal principal, @ModelAttribute("name") String name
		){	
		System.out.println("Id in process-contact"+contact.getId());
		System.out.println("name in process-contact: "+principal.getName());//here principal.getName() is the email id of that contact
	
		User user = this.userRepo.getUserByUserName(principal.getName());
		contact.setUser(user);
		this.contRepo.save(contact);
		
//		url looks like ---> /user/contact_details/460
		return "redirect:/user/contact_details/"+contact.getId();
	}
	
	@GetMapping("/profile")
	public String yourProfile(Model model, Principal principal) {
		User user=this.userRepo.getUserByUserName(principal.getName());
		model.addAttribute("user",user);
		return "usertemp/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings() {
		return "usertemp/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,
			Principal principal,HttpSession session){
		System.out.println(oldpassword+" "+newpassword);
		String username=principal.getName();
		User currentUser=this.userRepo.getUserByUserName(username);
		//the password we have encrypted
		//so if get this password it is not readable 
		System.out.println(currentUser.getPassword());
		if(passwordencoder.matches(oldpassword,currentUser.getPassword())){
			//change the password 
			currentUser.setPassword(passwordencoder.encode(newpassword));
			this.userRepo.save(currentUser);
			session.setAttribute("message", new Message("Password changed successfully","success"));
			System.out.println("changed successfully");
		}else {
			session.setAttribute("message", new Message("Please enter correct password","danger"));
			return "redirect:/user/settings";
		}	
		return "redirect:/user/index";
	}
	

}
