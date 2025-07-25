package scooter.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class BaseSpec {

    public static final RequestSpecification REQUEST_SPEC = new RequestSpecBuilder()
            .setBaseUri("https://qa-scooter.praktikum-services.ru")
            .addHeader("Content-type", "application/json")
            .build();
}
