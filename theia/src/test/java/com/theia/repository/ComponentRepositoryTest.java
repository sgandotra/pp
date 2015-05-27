package com.theia.repository;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ptaas.PtaasApplication;
import com.ptaas.repository.Component;
import com.ptaas.repository.ComponentRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PtaasApplication.class)
@Transactional
public class ComponentRepositoryTest {
	
	@Autowired
	private ComponentRepository componentRepository;
	
	private Component c;
	
	@Before
	public void setup() {
		c = new Component();
		c.setName("riskpaymentserv");
		
		
	}

	@Test
	public void testSaveS() {
		c = componentRepository.save(c);
		assertThat(c,is(componentRepository.findOne(c.getId())));
		assertEquals(1,componentRepository.count());
	}
	
	@Test(expected=DataIntegrityViolationException.class)
	public void testUniqComponent() {
		c = componentRepository.save(c);
		
		Component c1 = new Component();
		c1.setName("riskpaymentserv");
		//throw unique constraint
		c1 = componentRepository.save(c1);
		
	}

	@Test (expected=ConstraintViolationException.class)
	public void testNullComponentName() {
		c.setName(null);
		c = componentRepository.save(c);
	}
	

}
