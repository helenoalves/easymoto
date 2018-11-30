package org.easymoto.controllers.rest;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.easymoto.EasyMoto;
import org.easymoto.controllers.rest.response.ShortestResponse;
import org.easymoto.data.repository.CityRepository;
import org.easymoto.data.repository.DistanceRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.layout.StoreLayout;
import org.neo4j.kernel.internal.locker.StoreLocker;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EasyMoto.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.test.properties")
@Transactional
public class CityRestTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private Session session;
	
	@Value("${spring.data.neo4j.uri}")
	private URI neo4jUri;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private DistanceRepository distanceRepository;

	private URL base;

	@After
	public void tearDown() throws IOException {
		session.purgeDatabase();
		try (StoreLocker lock = new StoreLocker(new DefaultFileSystemAbstraction(),
				StoreLayout.of(Paths.get(neo4jUri).toFile()))) {
			lock.checkLock();
		}
	}

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/router");
	}

	@Test
	public void shouldAddOne() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<String> requestJson = new HttpEntity<String>(
				"[{ \"id\": \"6\", \"name\": \"Chinatown\", \"Operation\": \"ADD\", \"distance\": 90, \"to_id\": 3 }]",
				headers);

		ResponseEntity<String> entity = this.template.postForEntity(this.base.toString() + "/city", requestJson,
				String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("Operations executed over 1 cities.");
	}

	@Test
	public void shouldDeleteOne() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<String> requestJson = new HttpEntity<String>(
				"[{ \"id\": \"6\", \"name\": \"Chinatown\", \"Operation\": \"DEL\", \"distance\": 90, \"to_id\": 3 }]",
				headers);

		ResponseEntity<String> entity = this.template.postForEntity(this.base.toString() + "/city", requestJson,
				String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("Operations executed over 1 cities.");
	}

	@Test
	public void shouldGetDistance() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<String> requestJson = new HttpEntity<String>(
				"[{ \"id\": \"1\", \"name\": \"Porto Alegre\", \"Operation\": \"ADD\", \"distance\": 50, \"to_id\": 2 },"
						+ "{ \"id\": \"2\", \"name\": \"Gravatai\", \"Operation\": \"ADD\", \"distance\": 20, \"to_id\": 3 },"
						+ "{ \"id\": \"3\", \"name\": \"São Leopoldo\", \"Operation\": \"ADD\", \"distance\": 40, \"to_id\": 1 }]",
				headers);

		ResponseEntity<String> entityAdd = this.template.postForEntity(this.base.toString() + "/city", requestJson,
				String.class);

		then(entityAdd.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entityAdd.getBody()).isEqualTo("Operations executed over 3 cities.");

		ResponseEntity<ShortestResponse> entityPath = this.template.getForEntity(
				this.base.toString() + "/city/shortest/Porto Alegre/to/São Leopoldo", ShortestResponse.class);

		then(entityPath.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entityPath.getBody().getTotal()).isEqualTo("70");
	}
}
