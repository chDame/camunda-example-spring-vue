package io.camunda.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath*:/models/*.*")
public class CamundaExamplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamundaExamplesApplication.class, args);
	}

}
