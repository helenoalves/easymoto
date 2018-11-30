package org.easymoto.controllers.rest;

import java.util.List;

import org.easymoto.controllers.rest.request.CityRequest;
import org.easymoto.controllers.rest.response.ShortestResponse;
import org.easymoto.data.model.City;
import org.easymoto.data.repository.operations.OperationStrategy;
import org.easymoto.service.ShortestDistanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityRest {

	private Logger log = LoggerFactory.getLogger(CityRest.class);

	@Autowired
	private ShortestDistanceService shortestService;

	@Autowired
	private BeanFactory factory;

	@CrossOrigin
	@PostMapping
	public ResponseEntity<String> store(@RequestBody(required = true) @Validated List<CityRequest> cityRequestBody) {
		log.debug(cityRequestBody.toString());

		cityRequestBody.stream().forEach(aRequestBody -> {
			factory.getBean(aRequestBody.getOperation().component(), OperationStrategy.class)
					.execute(aRequestBody.getCity(), aRequestBody.getDistance());
		});
		return new ResponseEntity<String>(String.format("Operations executed over %s cities.", cityRequestBody.size()),
				HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/shortest/{origin}/to/{destiny}")
	public ResponseEntity<ShortestResponse> shortest(@PathVariable("origin") @Validated String origin,
			@PathVariable("destiny") @Validated String destiny) {
		City cityOrigin = City.builder().name(origin).build();
		City cityDestiny = City.builder().name(destiny).build();
		log.debug(cityOrigin.toString());
		log.debug(cityDestiny.toString());

		return new ResponseEntity<ShortestResponse>(shortestService.calculateShortest(cityOrigin, cityDestiny),
				HttpStatus.OK);
	}

}
