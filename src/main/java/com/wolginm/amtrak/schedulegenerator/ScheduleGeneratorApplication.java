package com.wolginm.amtrak.schedulegenerator;

import com.wolginm.amtrak.data.EnableAmtrakData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAmtrakData
public class ScheduleGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleGeneratorApplication.class, args);
	}

}
