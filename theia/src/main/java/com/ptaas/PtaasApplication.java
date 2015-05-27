package com.ptaas;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("com.theia")
@EnableAutoConfiguration
@PropertySource("classpath:database.properties")
@PropertySource("classpath:application.properties")
@Import({SecurityConfig.class})
@EnableTransactionManagement
public class PtaasApplication extends WebMvcConfigurerAdapter {

	@Value("${jdbc.driverClassName}")
	private String jdbcDriverClassName;
	
	@Value("${jdbc.url}")
	private String jdbcUrl;
	
	@Value("${db.username}")
	private String dbUserName;
	
	
	@Value("${db.password}")
	private String dbPassword;
	
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
	    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
	    builder.indentOutput(true).failOnEmptyBeans(false);
	    return builder;
	}
	
	@Bean
	public BasicDataSource dataSource() {
		
		BasicDataSource dataSource = new BasicDataSource();
		
		dataSource.setDriverClassName(jdbcDriverClassName);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(dbUserName);
		dataSource.setPassword(dbPassword);
				
		
		return dataSource;
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		vendorAdapter.setDatabase(Database.MYSQL);
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPackagesToScan("com.theia.repository");
		
		factory.afterPropertiesSet();
		
		return factory.getObject();
		
	}
	
	
	
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(emf);
		
		return txManager;
	}
	
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
      //  registry.addViewController("/").setViewName("home");
       // registry.addViewController("/home").setViewName("home");
    }
	

    public static void main(String[] args) {
        SpringApplication.run(PtaasApplication.class, args);
    }
    
    
    
}
