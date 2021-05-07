package com.search.repository.Impl;

import com.search.model.Restaurant;
import com.search.model.RestaurantWithDisField;
import com.search.repository.RestaurantRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public RestaurantRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Retry(name = "findRestaurantsNearPoint", fallbackMethod = "findFallbackRestaurantsNear")
    public List<Restaurant> findRestaurantsNearPoint(double lat, double lon, double distance) {
        return mongoTemplate.find(new Query(Criteria.where("location")
                .nearSphere(new GeoJsonPoint(lat, lon))
                .maxDistance(distance)), Restaurant.class);
    }

    //return mongoTemplate.geoNear(nearQuery, Restaurant.class);
    @Override
    @Retry(name = "findRestaurantsNear", fallbackMethod = "findFallbackRestaurantsNear")
    public GeoResults<RestaurantWithDisField> findRestaurantsNear(GeoJsonPoint point, Distance distance) {
        final NearQuery nearQuery = NearQuery.near(point)
                .maxDistance(distance)
                .spherical(true);

        return mongoTemplate.query(Restaurant.class)
                .as(RestaurantWithDisField.class)
                .near(nearQuery)
                .all();

    }

    public GeoResults<Restaurant> findFallbackRestaurantsNear(GeoJsonPoint point, Distance distance, Exception e){
        log.info("Failed to fetch valid data after multiple retries");
        return new GeoResults<>(null);
    }
}
