package scooter.test;

import scooter.client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Courier API")
@Feature("Delete Courier")
public class CourierDeleteTests {

    private final CourierClient courierClient = new CourierClient();

    @Test
    @Story("Positive: Delete existing courier")
    @Description("Создаёт курьера и успешно удаляет его по id. Ожидается ok: true и статус 200.")
    public void shouldDeleteCourierSuccessfullyTest() {
        String login = "courier_" + System.currentTimeMillis();
        String password = "1234";
        String firstName = "Ivan";

        createCourier(login, password, firstName);
        int courierId = loginCourier(login, password);
        deleteCourier(courierId);
    }

    @Test
    @Story("Negative: Delete without id")
    @Description("Проверяет, что если не указать id, возвращается ошибка 400")
    public void shouldFailToDeleteWithoutIdTest() {
        deleteCourierWithoutId();
    }

    @Test
    @Story("Negative: Delete with non-existent id")
    @Description("Проверяет, что если передать несуществующий id, возвращается ошибка 404")
    public void shouldFailToDeleteNonexistentCourierTest() {
        int nonexistentId = 999999;
        deleteCourierExpectingNotFound(nonexistentId);
    }

    @Step("Создание курьера с логином: {login}")
    private void createCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(201);
    }

    @Step("Логин курьера: {login}")
    private int loginCourier(String login, String password) {
        return courierClient.loginCourier(login, password);
    }

    @Step("Удаление курьера по id: {courierId}")
    private void deleteCourier(int courierId) {
        courierClient.deleteCourier(courierId)
                .statusCode(200)
                .body("ok", is(true));
    }

    @Step("Удаление курьера без id")
    private void deleteCourierWithoutId() {
        courierClient.deleteCourierWithoutId()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Step("Удаление несуществующего курьера с id: {courierId}")
    private void deleteCourierExpectingNotFound(int courierId) {
        courierClient.deleteCourier(courierId)
                .statusCode(404)
                .body("message", containsString("Курьера с таким id нет"));
    }
}
