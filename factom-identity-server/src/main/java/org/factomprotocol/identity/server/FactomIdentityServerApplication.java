package org.factomprotocol.identity.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = {"org.blockchain_innovation.factom", "org.factomprotocol.identity.server"})
@Configuration
public class FactomIdentityServerApplication {


	public static void main(String[] args) {
		run(FactomIdentityServerApplication.class, args);
	}

}
