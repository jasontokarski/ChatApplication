package com.capgemini.config;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@PropertySource(value = {"classpath:database.properties"})
public class DBConfig {

	@Autowired
	private Environment env;

	//Setup logger for diagnostics
	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Bean
	public DataSource getDataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		//log the connection props
		logger.info(">>> db.url=" + env.getProperty("db.jdbcUrl"));
		logger.info(">>> db.user=" + env.getProperty("db.username"));
		
		dataSource.setDriverClassName(env.getProperty("db.driver"));
		
		//Set database connection props
		dataSource.setUrl(env.getProperty("db.jdbcUrl"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));
		
		return dataSource;
	}
}
