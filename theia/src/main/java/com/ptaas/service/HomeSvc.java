package com.ptaas.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ptaas.repository.User;
import com.ptaas.repository.UserRepository;


@Service("homeSvc")
public class HomeSvc {
	
	
	@Autowired
	private UserRepository userRepository;

	public User saveUserDetails(Principal principal) {
		
		Assert.notNull(principal, " Principal cannot be null");
		
		LdapUserDetailsImpl userDetails = (LdapUserDetailsImpl)((UsernamePasswordAuthenticationToken)principal).getPrincipal();
		
		String userName = userDetails.getUsername();
		
		User user = userRepository.findByUserName(userName);
		
		if(null == user) {
			user = new User();
			user.setUserName(userName);
			
		} 
		user.setLastUpdated(System.currentTimeMillis());
		user = userRepository.save(user);
		
		return user;
	}
	
}
