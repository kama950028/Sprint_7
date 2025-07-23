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
@Feature("Create Courier")
public class CreateCourierTests {

    private final CourierClient courierClient = new CourierClient();

    @Test
    @Story("Positive: Create new courier")
    @Description("Создание курьера с валидными данными. Ожидается статус 201 и ok: true")
    public void shouldCreateNewCourierTest() {
        String login = generateLogin();
        String password = "1234";
        String firstName = "Test";

        createCourier(login, password, firstName);
        deleteCourierAfterTest(login, password); // cleanup
    }

    @Test
    @Story("Negative: Create courier with existing login")
    @Description("Создание курьера с логином, который уже существует. Ожидается ошибка 409.")
    public void shouldFailToCreateDuplicateCourierTest() {
        String login = generateLogin();
        String password = "1234";
        String firstName = "Test";

        createCourier(login, password, firstName);
        createCourierExpectingConflict(login, password, firstName);
        deleteCourierAfterTest(login, password); // cleanup
    }

    @Test
    @Story("Negative: Create courier without required fields")
    @Description("Создание курьера без логина. Ожидается ошибка 400.")
    public void shouldFailWithoutLoginTest() {
        createCourierWithoutLogin();
    }

    @Step("Создать курьера: login={login}")
    private void createCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(201)
                .body("ok", is(true));
    }

    @Step("Создать курьера повторно, ожидая ошибку 409")
    private void createCourierExpectingConflict(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Step("Создать курьера без логина")
    private void createCourierWithoutLogin() {
        courierClient.createCourier(null, "1234", "Test")
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Step("Удаление курьера после теста по логину: {login}")
    private void deleteCourierAfterTest(String login, String password) {
        int id = courierClient.loginCourier(login, password);
        courierClient.deleteCourier(id).statusCode(200);
    }

    @Step("Генерация логина")
    private String generateLogin() {
        return "courier_" + System.currentTimeMillis();
    }
}
