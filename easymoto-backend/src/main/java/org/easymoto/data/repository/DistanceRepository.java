package org.easymoto.data.repository;

import java.util.Collection;

import org.easymoto.data.model.Distance;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface DistanceRepository extends Neo4jRepository<Distance, Long> {

	@Query("MATCH p=(origin:City { id: {originId} })-[r:distanceOf]->(destiny:City { id: {destinyId} }) RETURN p")
	Collection<Distance> findByToCityFromCity(@Param("originId") String originId, @Param("destinyId") String destinyId);

	@Query("MATCH (origin:City { name: {originName} }), (destiny:City { name: {destinyName} }), p = shortestPath((origin)-[*..15]->(destiny)) RETURN relationships(p)")
	Collection<Distance> findShortestPath(@Param("originName") String originName,
			@Param("destinyName") String destinyName);

}
