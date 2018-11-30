package org.easymoto.controllers.rest;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.IOException;
import java.net.URL;

import org.easymoto.EasyMoto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EasyMoto.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties", properties = { "management.port=0" })
@Transactional
public class ActuatorTest {

	@Value("${local.management.port}")
	private int port;

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private Session session;

	private URL base;

	@After
	public void tearDown() throws IOException {
		session.purgeDatabase();
	}

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/router");
	}

	@Test
	public void shouldAddThree() throws Exception {
		ResponseEntity<String> entity = this.template.getForEntity(this.base.toString() + "/health", String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).contains("\"status\":\"UP\"");
	}
}
