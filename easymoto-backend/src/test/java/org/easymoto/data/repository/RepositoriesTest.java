package org.easymoto.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;

import org.easymoto.EasyMoto;
import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EasyMoto.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.test.properties")
@Transactional
public class RepositoriesTest {

	@Autowired
	private Session session;

	@Value("${spring.data.neo4j.uri}")
	private URI neo4jUri;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private DistanceRepository distanceRepository;

	private City cacimbinhasSul = City.builder().id("1").name("Cacimbinhas do Sul").build();

	private City cacimbinhasNorte = City.builder().id("2").name("Cacimbinhas do Norte").build();

	private City cidadelaOeste = City.builder().id("3").name("Cidadela do Oeste").build();

	private City pindamonhangaba = City.builder().id("4").name("Pindamonhangaba").build();

	@Before
	public void setUp() {
		session.purgeDatabase();
		cacimbinhasSul = cityRepository.save(cacimbinhasSul);
		cacimbinhasNorte = cityRepository.save(cacimbinhasNorte);
		cidadelaOeste = cityRepository.save(cidadelaOeste);
		pindamonhangaba = cityRepository.save(pindamonhangaba);

		City cityTo = cityRepository.findById(cacimbinhasNorte.getId()).get();
		distanceRepository.save(Distance.builder().fromCity(cacimbinhasSul).toCity(cityTo).value(20).build());

		cityTo = cityRepository.findById(cidadelaOeste.getId()).get();
		distanceRepository.save(Distance.builder().fromCity(cacimbinhasNorte).toCity(cityTo).value(30).build());
	}

	@After
	public void tearDown() throws IOException {
		session.purgeDatabase();
		try (StoreLocker lock = new StoreLocker(new DefaultFileSystemAbstraction(),
				StoreLayout.of(Paths.get(neo4jUri).toFile()))) {
			lock.checkLock();
		}
	}

	@Test
	public void testFindByName() {

		String name = "Cidadela do Oeste";
		City result = cityRepository.findByName(name);
		assertNotNull(result);
		assertEquals("3", result.getId());
	}

	@Test
	public void testFindByTitleContaining() {
		String name = "Cacimbinhas*";
		Collection<City> result = cityRepository.findByNameLike(name);
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void testShortestCitiesPath() {
		City origin = cityRepository.findByName(cacimbinhasSul.getName());
		City destiny = cityRepository.findByName(cidadelaOeste.getName());
		Collection<City> graph = cityRepository.findShortestPath(origin.getName(), destiny.getName());

		assertEquals(3, graph.size());
	}

	@Test
	public void testShortestDistancePath() {
		Collection<Distance> graph = distanceRepository.findShortestPath(cacimbinhasSul.getName(),
				cidadelaOeste.getName());

		Integer pathSum = graph.stream().mapToInt(aDistance -> {
			return aDistance.getValue();
		}).sum();

		assertEquals(50, pathSum.intValue());

	}

}