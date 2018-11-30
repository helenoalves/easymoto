package org.easymoto.data.repository.operations;

import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.springframework.stereotype.Component;

@Component
public interface OperationStrategy {

	void execute(City city, Distance distance);

}
