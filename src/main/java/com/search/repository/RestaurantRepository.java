package com.search.repository;

import com.search.model.Restaurant;
import com.search.model.RestaurantWithDisField;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface RestaurantRepository {

    GeoResults<RestaurantWithDisField> findRestaurantsNear(GeoJsonPoint point, Distance distance);
    List<Restaurant> findRestaurantsNearPoint(double lat, double lon, double distance);
}
