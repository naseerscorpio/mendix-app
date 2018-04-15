package com.mendix.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, RabbitAutoConfiguration.class})
public class MendixApp {

    private static final Logger LOG = LoggerFactory.getLogger(MendixApp.class);

    public static void main(String[] args) {

        LOG.info("*********************************************");
        LOG.info("***********STARTING MENDIX APP***************");
        LOG.info("*********************************************");

        SpringApplication.run(MendixApp.class, args);

        LOG.info("*********************************************");
        LOG.info("*******STARTED MENDIX APP SUCCESSFULLY*******");
        LOG.info("*********************************************");

    }

}
