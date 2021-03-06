package org.easymoto.data.repository.operations;

import java.util.Collection;

import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.easymoto.data.repository.CityRepository;
import org.easymoto.data.repository.DistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;

@Data
@Transactional
@Component(OperationAdd.COMPONENT_NAME)
public class OperationAdd implements OperationStrategy {

	public static final String COMPONENT_NAME = "OperationAdd";

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private DistanceRepository distanceRepository;

	@Override
	public void execute(City city, Distance distance) {
		distance.setFromCity(city);
		distance.setToCity(cityRepository.findById(distance.getToCity().getId()).orElse(distance.getToCity()));

		Collection<Distance> distances = distanceRepository.findByToCityFromCity(distance.getFromCity().getId(),
				distance.getToCity().getId());
		
		distanceRepository.save(distances.stream().findFirst().orElse(distance));
	}

}
