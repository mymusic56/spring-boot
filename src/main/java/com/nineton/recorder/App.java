package com.nineton.recorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@ComponentScan(basePackages = {"com.engine.hello.controller","com.engine.hello.entity","com.engine.hello.repository"})
public class App implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	public void run(String... args) throws Exception {

	}
}
