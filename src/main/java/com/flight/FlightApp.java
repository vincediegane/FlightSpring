package com.flight;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
	@PropertySource(value="classpath:messages_fr.properties", encoding="UTF-8"),
	@PropertySource(value="classpath:messages_en.properties", encoding="UTF-8")
})
public class FlightApp 
{
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
    public static void main( String[] args )
    {
    	SpringApplication.run(FlightApp.class, args);
    }
}
