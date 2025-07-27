package scooter.client;

import com.github.javafaker.Faker;
import scooter.model.OrderModel;

import java.time.LocalDate;

public class OrderGenerator {

    private static final Faker faker = new Faker();

    public static OrderModel generate(String[] color) {
        return new OrderModel(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.address().streetAddress(),
                faker.number().numberBetween(1, 5)+"",
                faker.phoneNumber().cellPhone(),
                2,
                LocalDate.now().plusDays(1).toString(),
                faker.lorem().sentence(),
                color
        );
    }
}
