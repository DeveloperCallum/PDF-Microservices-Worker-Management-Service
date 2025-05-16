package com.willcocks.callum.workermanagementservice;

import org.springframework.boot.SpringApplication;

public class TestWorkerManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(WorkerManagementServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
