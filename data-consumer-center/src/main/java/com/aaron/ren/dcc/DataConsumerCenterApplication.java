package com.aaron.ren.dcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author renshuaibing
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,MongoAutoConfiguration.class})
public class DataConsumerCenterApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(DataConsumerCenterApplication.class, args);
    }
}
