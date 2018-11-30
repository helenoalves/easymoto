package org.easymoto.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.easymoto.controllers.rest.response.ShortestResponse;
import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.easymoto.data.repository.CityRepository;
import org.easymoto.data.repository.DistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShortestDistanceService {
	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private DistanceRepository distanceRepository;

	public ShortestResponse calculateShortest(City cityOrigin, City cityDestiny) {
		cityOrigin = cityRepository.findByName(cityOrigin.getName());
		cityDestiny = cityRepository.findByName(cityDestiny.getName());

		Collection<City> citiesPath = cityRepository.findShortestPath(cityOrigin.getName(), cityDestiny.getName());
		Collection<Distance> distanciesPath = distanceRepository.findShortestPath(cityOrigin.getName(),
				cityDestiny.getName());
		Integer pathSum = distanciesPath.stream().mapToInt(aDistance -> {
			return aDistance.getValue();
		}).sum();

		return ShortestResponse.builder().from(citiesPath.stream().findFirst().orElse(cityOrigin))
				.to(citiesPath.stream().skip(1).collect(Collectors.toList())).total(pathSum.toString()).build();
	}

}
