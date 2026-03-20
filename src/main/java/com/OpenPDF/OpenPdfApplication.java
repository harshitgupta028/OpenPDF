package com.OpenPDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenPdfApplication {

	private static final Logger log = LoggerFactory.getLogger(OpenPdfApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OpenPdfApplication.class, args);
		log.info("OpenPDF Application started successfully.");
	}

}
