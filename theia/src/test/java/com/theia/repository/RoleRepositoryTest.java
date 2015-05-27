package com.theia.repository;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.ptaas.PtaasApplication;
import com.ptaas.repository.Component;
import com.ptaas.repository.ComponentRepository;
import com.ptaas.repository.Role;
import com.ptaas.repository.RoleRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PtaasApplication.class)
@Transactional
@TransactionConfiguration(defaultRollback=false)
public class RoleRepositoryTest {

	private static final String[] components = {
		"riskpaymentserv",
		"paymentserv",
		"helixserv",
		"helixinternalserv",
		"posapiserv",
		"adaptivepaymentserv",
		"riskreadserv",
		"riskanalyticserv",
		"occ",
		"betahubspartaweb",
		"merchantpaymentspartaweb"
	};
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ComponentRepository componentRepository;

	private Set<Component> cs;
	
	private Iterable<Component> collection;
	
/*	@Before
	public void setUp() throws Exception {
		
		cs			   = new HashSet<Component>();
		for(String componentName : components) {
			Component c = new Component();
			c.setName(componentName);
			cs.add(c);
		}
		collection = componentRepository.save(cs);
	}
	*/

	@Test(expected=ConstraintViolationException.class)
	public void testSaveRolesWithNullComponents() {
		assertEquals(components.length,componentRepository.count());
		
		Role r = new Role();
		r.setName("role1");
		roleRepository.save(r);
	}
	
	@Test
	public void testSaveRolesWithComponents() {
		Role r = new Role();
		r.setName("role1");
		r.setComponents((Set<Component>) collection);
		r = roleRepository.save(r);
		
		assertEquals(1,roleRepository.count());
		assertEquals(components.length,componentRepository.count());
		
		Collection<Component> list = roleRepository.findOne(r.getId()).getComponents();
		
		assertThat(cs,is(list));
		
	}
	
	@Test
	public void testDeleteRoleLeavesComponentsIntact() throws InterruptedException {
		
		Role r = new Role();
		r.setName("role1");
		r.setComponents(cs);
		r = roleRepository.save(r);
		
		//delete role
		roleRepository.delete(r.getId());
		assertNull(roleRepository.findOne(r.getId()));
		
		//component should still be there
		Collection<Component> collection = (Collection<Component>) componentRepository.findAll();
		
		assertEquals(cs.size(),collection.size());
	}
	
	@Test
	@Transactional
	public void testSaveMultipleRolesWithComponents() {
	/*	Role r = new Role();
		r.setName("role1");
		r.setComponents(cs);
		r = roleRepository.save(r);
				
		assertEquals(1,roleRepository.count());
		assertEquals(components.length,componentRepository.count());
		
		Collection<Component> list = roleRepository.findOne(r.getId()).getComponents();
		
		assertThat(cs,is(list));
		*/
		
		Component c1 = componentRepository.findByName("riskpaymentserv");
		Component c2 = componentRepository.findByName("riskanalyticserv");
		
		Set<Component> newcs = new HashSet<Component>();
		newcs.add(c1);
		newcs.add(c2);
		
		Role r2 = new Role();
		r2.setName("role2");
		r2.setComponents(newcs);
		r2 = roleRepository.save(r2);
		
		assertThat(newcs,is(roleRepository.findOne(r2.getId()).getComponents()));
	
		
	}

}


