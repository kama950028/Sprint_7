package scooter.test;

import scooter.client.CourierClient;
import io.qameta.allure.*;
import com.github.javafaker.Faker;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Courier API")
@Feature("Courier Login")
public class LoginCourierTests {

    private final CourierClient courierClient = new CourierClient();
    private final Faker faker = new Faker();

    @Test
    @DisplayName("Успешная авторизация курьера с валидными данными")
    @Story("Positive: Successful login")
    @Description("Создание и логин курьера с валидными данными. Ожидается код 200 и id в ответе.")
    public void shouldLoginSuccessfullyTest() {
        String login = faker.name().username();
        String password = faker.internet().password();
        String firstName = faker.name().firstName();

        createCourier(login, password, firstName);
        loginCourierAndCheckId(login, password);
        deleteCourierAfterTest(login, password);
    }

    @Test
    @DisplayName("Ошибка при авторизации без логина")
    @Story("Negative: Missing login")
    @Description("Попытка авторизации без логина. Ожидается ошибка 400.")
    public void shouldFailWithoutLoginTest() {
        String password = faker.internet().password();
        loginWithoutLogin(password);
    }

    @Test
    @DisplayName("Ошибка при авторизации без пароля")
    @Story("Negative: Missing password")
    @Description("Попытка авторизации без пароля. Ожидается ошибка 400.")
    public void shouldFailWithoutPasswordTest() {
        String login = faker.name().username();
        loginWithoutPassword(login);
    }

    @Test
    @DisplayName("Ошибка при авторизации с несуществующими данными")
    @Story("Negative: Wrong credentials")
    @Description("Попытка авторизации с неверными данными. Ожидается ошибка 404.")
    public void shouldFailWithWrongCredentialsTest() {
        courierClient.loginCourierRaw("non_existing_login", "wrong_password")
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Step("Создание курьера: login={login}, password={password}, firstName={firstName}")
    private void createCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(SC_CREATED);
    }

    @Step("Логин курьера: login={login}")
    private void loginCourierAndCheckId(String login, String password) {
        courierClient.loginCourierRaw(login, password)
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Step("Удаление курьера после теста: login={login}")
    private void deleteCourierAfterTest(String login, String password) {
        int id = courierClient.loginCourier(login, password);
        courierClient.deleteCourier(id).statusCode(SC_OK);
    }

    @Step("Попытка логина без логина")
    private void loginWithoutLogin(String password) {
        courierClient.loginCourierRaw(null, password)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Step("Попытка логина без пароля")
    private void loginWithoutPassword(String login) {
        courierClient.loginCourierRaw(login, null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для входа"));
    }
}
