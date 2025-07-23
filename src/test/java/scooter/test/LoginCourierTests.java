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
@Feature("Courier Login")
public class LoginCourierTests {

    private final CourierClient courierClient = new CourierClient();

    @Test
    @Story("Positive: Successful login")
    @Description("Создание и логин курьера с валидными данными. Ожидается код 200 и id в ответе.")
    public void shouldLoginSuccessfullyTest() {
        String login = generateLogin();
        String password = "1234";
        String firstName = "LoginTest";

        createCourier(login, password, firstName);
        loginCourierAndCheckId(login, password);
        deleteCourierAfterTest(login, password);
    }

    @Test
    @Story("Negative: Missing login")
    @Description("Попытка авторизации без логина. Ожидается ошибка 400.")
    public void shouldFailWithoutLoginTest() {
        courierClient.loginCourierRaw(null, "1234")
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    @Story("Negative: Missing password")
    @Description("Попытка авторизации без пароля. Ожидается ошибка 400.")
    public void shouldFailWithoutPasswordTest() {
        courierClient.loginCourierRaw("some_login", null)
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    @Story("Negative: Wrong credentials")
    @Description("Попытка авторизации с неверными данными. Ожидается ошибка 404.")
    public void shouldFailWithWrongCredentialsTest() {
        courierClient.loginCourierRaw("non_existing_login", "wrong_password")
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }


    @Step("Создание курьера: {login}")
    private void createCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(201);
    }

    @Step("Логин курьера: {login}")
    private void loginCourierAndCheckId(String login, String password) {
        courierClient.loginCourierRaw(login, password)
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Удаление курьера после теста")
    private void deleteCourierAfterTest(String login, String password) {
        int id = courierClient.loginCourier(login, password);
        courierClient.deleteCourier(id).statusCode(200);
    }

    @Step("Генерация логина")
    private String generateLogin() {
        return "courier_" + System.currentTimeMillis();
    }
}
