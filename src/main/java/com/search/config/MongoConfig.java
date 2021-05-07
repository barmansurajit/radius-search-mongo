package com.search.config;

import com.mongodb.MongoClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.ZonedDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class MongoConfig {

    private static final int MIN_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final int READ_TIMEOUT = 5000;
    private static final int MAX_WAIT_TIME = 10000;
    private static final int CONNECT_TIMEOUT = 5000;

    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> builder
                        .maxWaitTime(MAX_WAIT_TIME, MILLISECONDS)
                        .minSize(MIN_SIZE)
                        .maxSize(MAX_SIZE))
                .applyToSocketSettings(builder -> builder
                        .readTimeout(READ_TIMEOUT, MILLISECONDS)
                        .connectTimeout(CONNECT_TIMEOUT, MILLISECONDS))
                .build();
    }


    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory){
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @ReadingConverter
    private static class ZoneDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(Date date) {
            return date.toInstant().atZone(UTC);
        }
    }

    @WritingConverter
    private static class ZoneDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
        @Override
        public Date convert(ZonedDateTime zonedDateTime) {
            return Date.from(zonedDateTime.toInstant());
        }
    }
}