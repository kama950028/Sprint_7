package scooter.test;

import io.qameta.allure.junit4.DisplayName;
import scooter.client.CourierClient;
import scooter.model.CourierModel;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


@Epic("Courier API")
@Feature("Delete Courier")
public class CourierDeleteTests {

    private final CourierClient courierClient = new CourierClient();
    private final Faker faker = new Faker();

    @Test
    @Story("Positive: Delete existing courier")
    @DisplayName("Удаление курьера по валидному ID")
    @Description("Создаёт курьера и успешно удаляет его по id. Ожидается ok: true и статус 200.")
    public void shouldDeleteCourierSuccessfullyTest() {
        CourierModel courier = generateCourier();
        createCourier(courier);
        int courierId = loginCourier(courier);
        deleteCourier(courierId);
    }

    @Test
    @Story("Negative: Delete without id")
    @DisplayName("Удаление курьера без указания ID")
    @Description("Проверяет, что если не указать id, возвращается ошибка 400")
    public void shouldFailToDeleteWithoutIdTest() {
        deleteCourierWithoutId();
    }

    @Test
    @Story("Negative: Delete with non-existent id")
    @DisplayName("Удаление курьера по несуществующему ID")
    @Description("Проверяет, что если передать несуществующий id, возвращается ошибка 404")
    public void shouldFailToDeleteNonexistentCourierTest() {
        int nonexistentId = 999999;
        deleteCourierExpectingNotFound(nonexistentId);
    }

    @Step("Создание курьера: {0}")
    private void createCourier(CourierModel courier) {
        courierClient.createCourier(courier)
                .statusCode(SC_CREATED);
    }

    @Step("Логин курьера: {0}")
    private int loginCourier(CourierModel courier) {
        return courierClient.loginCourier(courier.getLogin(), courier.getPassword());
    }

    @Step("Удаление курьера по id: {0}")
    private void deleteCourier(int courierId) {
        courierClient.deleteCourier(courierId)
                .statusCode(SC_OK)
                .body("ok", is(true));
    }

    @Step("Удаление курьера без id")
    private void deleteCourierWithoutId() {
        courierClient.deleteCourierWithoutId()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Step("Удаление несуществующего курьера с id: {0}")
    private void deleteCourierExpectingNotFound(int courierId) {
        courierClient.deleteCourier(courierId)
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Курьера с таким id нет"));
    }

    @Step("Генерация данных курьера через Faker")
    private CourierModel generateCourier() {
        String login = "courier_" + faker.number().digits(6);
        String password = faker.internet().password();
        String firstName = faker.name().firstName();
        return new CourierModel(login, password, firstName);
    }
}
