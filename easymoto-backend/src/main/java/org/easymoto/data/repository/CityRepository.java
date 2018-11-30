package org.easymoto.data.repository;

import java.util.Collection;

import org.easymoto.data.model.City;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends Neo4jRepository<City, String> {

	City findByName(@Param("name") String name);

	Collection<City> findByNameLike(@Param("name") String name);

	@Query("MATCH (origin:City { name: {originName} }), (destiny:City { name: {destinyName} }), p = shortestPath((origin)-[*..15]->(destiny)) RETURN p")
	Collection<City> findShortestPath(@Param("originName") String originName, @Param("destinyName") String destinyName);

}
