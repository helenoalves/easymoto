package org.easymoto;

import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.easymoto.data.repository.CityRepository;
import org.easymoto.data.repository.DistanceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EntityScan(basePackages = "org.easymoto.data.model")
@EnableNeo4jRepositories
public class EasyMoto {

	public static void main(String[] args) {
		SpringApplication.run(EasyMoto.class, args);
	}

	@Bean
	CommandLineRunner demo(CityRepository cityRepository, DistanceRepository distanceRepository) {
		return args -> {

			cityRepository.deleteAll();
			distanceRepository.deleteAll();

			City newYork = City.builder().id(Integer.toString(102)).name("New York").build();
			cityRepository.save(newYork);
			
			City washington = City.builder().id(Integer.toString(101)).name("Washington").build();
			cityRepository.save(washington);

			City philadelphia = City.builder().id(Integer.toString(103)).name("Philadelphia").build();
			cityRepository.save(philadelphia);

			Distance newYorkToWashington = Distance.builder().value(60).fromCity(newYork).toCity(washington).build();
			distanceRepository.save(newYorkToWashington);

			Distance washingtonToPhiladelphia = Distance.builder().value(60).fromCity(washington).toCity(philadelphia)
					.build();
			distanceRepository.save(washingtonToPhiladelphia);

			newYork = cityRepository.findByName(washington.getName());

		};
	}
}