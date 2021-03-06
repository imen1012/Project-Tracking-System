package com.pfa.pack.controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pfa.pack.models.entities.UserCredential;
import com.pfa.pack.services.EmployeeService;
import com.pfa.pack.services.UserCredentialService;
import com.pfa.pack.utils.email.EmailUtil;
import com.pfa.pack.utils.sms.Sms;
import com.pfa.pack.utils.sms.SmsUtil;

@Controller
@Lazy
@RequestMapping(value = {"/app/credentials"})
public class UserCredentialController {
	
	private final UserCredentialService userCredentialService;
	private final EmployeeService employeeService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final EmailUtil emailUtil;
	private final SmsUtil smsUtil;
	private static final Logger logger = LoggerFactory.getLogger(UserCredentialController.class);
	
	static {
		logger.info("************ entering " + UserCredentialController.class.getName() + " ************");
	}
	
	/**
	 * Inject dependencies
	 * @param userCredentialService
	 * @param employeeService
	 * @param bCryptPasswordEncoder
	 * @param emailUtil
	 * @param smsUtil
	 */
	@Autowired
	public UserCredentialController(final UserCredentialService userCredentialService, final EmployeeService employeeService, final BCryptPasswordEncoder bCryptPasswordEncoder, final EmailUtil emailUtil, final SmsUtil smsUtil) {
		this.userCredentialService = userCredentialService;
		this.employeeService = employeeService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.emailUtil = emailUtil;
		this.smsUtil = smsUtil;
	}
	
	/**
	 * display credential-edit view
	 * @param employeeId
	 * @param model
	 * @return credential-edit
	 */
	@GetMapping(value = {"/credential-edit"})
	public String displayCredentialEdit(@RequestParam("employeeId") final String employeeId, final Model model) {
		model.addAttribute("username", this.employeeService.findById(Integer.parseInt(employeeId)).getUserCredential().getUsername());
		return "credentials/credential-edit";
	}
	
	/**
	 * handle credential-edit business logic in order to change credentials
	 * @param username
	 * @param pwd1
	 * @param pwd2
	 * @param model
	 * @return credential-edit
	 */
	@PostMapping(value = {"/credential-edit"})
	public String handleCredentialEdit(@RequestParam("username") String username, @RequestParam("pwd1") final String pwd1, @RequestParam("pwd2") final String pwd2, final Model model) {
		
		final boolean isBlank = username.isBlank() || pwd1.isEmpty() || pwd2.isEmpty();
		
		if (isBlank) {
			model.addAttribute("msg", "Field(s) is/are empty");
			model.addAttribute("msgColour", "danger");
			return "credentials/credential-edit";
		}
		else {
			
			if (!pwd1.equals(pwd2)) {
				model.addAttribute("msg", "Passwords not matching, try again!");
				model.addAttribute("msgColour", "danger");
				return "credentials/credential-edit";
			}
			
		}
		
		final UserCredential userCredential = this.userCredentialService.findByUsername(username);
		userCredential.setUsername(username);
		userCredential.setPassword(this.bCryptPasswordEncoder.encode(pwd1));
		
		final String msg = "You've changed some credentials : " + LocalDateTime.now() + "\n" + "Username : " + username + "\n" + "Password : " + pwd1;
		
		this.userCredentialService.update(userCredential);
		logger.info("Credentials updated successfully");
		
		this.emailUtil.sendEmail(userCredential.getEmployee().getEmail(), "Project-Tracker-Sys", msg);
		logger.info("MAIL successfully sent to {}", userCredential.getEmployee().getEmail());
		
		this.smsUtil.sendSms(new Sms(userCredential.getEmployee().getPhone(), msg));
		logger.info("SMS successfully sent to {}", userCredential.getEmployee().getPhone());
		
		model.addAttribute("msg", "Credentials updated successfully");
		model.addAttribute("msgColour", "success");
		
		return "credentials/credential-edit";
	}
	
	
	
}









