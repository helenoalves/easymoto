package org.easymoto.controllers.rest.response;

import java.util.List;

import org.easymoto.data.model.City;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortestResponse {

	@JsonProperty("To")
	List<City> to;
	@JsonProperty("From")
	City from;
	@JsonProperty("Total")
	String total;
}
