package com.isteer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiTenantHrManagementApplication implements CommandLineRunner {
     
	public static  Logger logging = LogManager.getLogger(MultiTenantHrManagementApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(MultiTenantHrManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		logging.info("Application is starting");
		logging.warn("While application gets Started, User should be alret for performing the operations");
					
	}

}
