package org.easymoto.repository.operations;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import org.easymoto.EasyMoto;
import org.easymoto.data.model.City;
import org.easymoto.data.model.Distance;
import org.easymoto.data.repository.CityRepository;
import org.easymoto.data.repository.operations.OperationAdd;
import org.easymoto.data.repository.operations.OperationStrategy;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.layout.StoreLayout;
import org.neo4j.kernel.internal.locker.StoreLocker;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.BeanFactory;
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
public class OperationAddTest {

	@Autowired
	private Session session;

	@Value("${spring.data.neo4j.uri}")
	private URI neo4jUri;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private BeanFactory factory;

	private City operationAddCity1 = City.builder().id("1").name("Operation Add - City 1").build();

	private City operationAddCity2 = City.builder().id("2").name("Operation Add - City 2").build();

	private Distance city1ToCity2 = Distance.builder().fromCity(operationAddCity1).toCity(operationAddCity2).value(10)
			.build();

	@After
	public void tearDown() throws IOException {
		session.purgeDatabase();
		try (StoreLocker lock = new StoreLocker(new DefaultFileSystemAbstraction(),
				StoreLayout.of(Paths.get(neo4jUri).toFile()))) {
			lock.checkLock();
		}
	}

	@Test
	public void testSimpleExecute() {
		OperationStrategy addOp = factory.getBean(OperationAdd.COMPONENT_NAME, OperationStrategy.class);

		addOp.execute(operationAddCity1, Distance.builder().toCity(City.builder().id("2").build()).value(10).build());
	}

	@Test
	public void testCityDistanceExecute() {
		OperationStrategy addOp = factory.getBean(OperationAdd.COMPONENT_NAME, OperationStrategy.class);

		addOp.execute(operationAddCity2, Distance.builder().toCity(City.builder().id("3").build()).value(20).build());
		addOp.execute(operationAddCity1, city1ToCity2);
	}

}
