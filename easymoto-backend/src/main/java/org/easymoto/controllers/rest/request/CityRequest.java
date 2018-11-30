package org.easymoto.controllers.rest.request;

import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.easymoto.data.model.Operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CityRequest {
	City city;
	Distance distance;
	Operation operation;

	@JsonCreator
	public CityRequest(@JsonProperty("id") String id, @JsonProperty("name") String name,
			@JsonProperty("distance") String distance, @JsonProperty("to_id") String toId,
			@JsonProperty("Operation") String operation) {
		this.city = City.builder().id(id).name(name).build();
		this.distance = Distance.builder().value(Integer.valueOf(distance)).toCity(City.builder().id(toId).build())
				.build();
		this.operation = Operation.valueOf(operation);
	}
}
