package com.search;

import com.search.model.Restaurant;
import com.search.model.RestaurantWithDisField;
import com.search.repository.RestaurantRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.geo.Metrics.MILES;

@Slf4j
@SpringBootApplication
public class Application {

    private RestaurantRepository restaurantRepository;

    @Autowired
    public Application(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent>  applicationListener(){
        return applicationReadyEvent -> {

            GeoJsonPoint point = new GeoJsonPoint(-73.93414657, 40.82302903);
            Distance distance = new Distance(0.19, MILES);

            List<@NonNull RestaurantWithDisField> restaurants = restaurantRepository.findRestaurantsNear(point, distance)
                    .getContent()
                    .stream()
                    .map(GeoResult::getContent)
                    .collect(collectingAndThen(toList(), Collections::unmodifiableList));


            log.info("Number of restaurants - {}", restaurants.size());

            restaurants.forEach(restaurant -> {
                log.info("Restaurant - {}", restaurant);
            });

            log.info("-------------------------------");

            List<Restaurant> restaurantsNearPoint = restaurantRepository.findRestaurantsNearPoint(-73.93414657, 40.82302903, 305.775328517);

            log.info("### Number of restaurants - {}", restaurantsNearPoint.size());

            restaurantsNearPoint.forEach(restaurant -> {
                log.info("### Restaurant - {}", restaurant);
            });

        };
    }
}
