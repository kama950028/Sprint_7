package scooter.test;

import scooter.client.CourierClient;
import io.qameta.allure.*;
import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import scooter.model.CourierModel;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Courier API")
@Feature("Create Courier")
public class CreateCourierTests {

    private final CourierClient courierClient = new CourierClient();
    private final Faker faker = new Faker();

    private String testLogin;
    private String testPassword;

    @Test
    @DisplayName("Создание нового курьера — успешный сценарий")
    @Story("Positive: Create new courier")
    @Description("Создание курьера с валидными данными. Ожидается статус 201 и ok: true")
    public void shouldCreateNewCourierTest() {
        testLogin = faker.name().username();
        testPassword = faker.internet().password();
        String firstName = faker.name().firstName();

        createCourier(testLogin, testPassword, firstName);
    }

    @Test
    @DisplayName("Создание курьера с уже существующим логином — ошибка 409")
    @Story("Negative: Create courier with existing login")
    @Description("Создание курьера с логином, который уже существует. Ожидается ошибка 409.")
    public void shouldFailToCreateDuplicateCourierTest() {
        testLogin = faker.name().username();
        testPassword = faker.internet().password();
        String firstName = faker.name().firstName();

        createCourier(testLogin, testPassword, firstName);
        createCourierExpectingConflict(testLogin, testPassword, firstName);
    }

    @Test
    @DisplayName("Создание курьера без логина — ошибка 400")
    @Story("Negative: Create courier without required fields")
    @Description("Создание курьера без логина. Ожидается ошибка 400.")
    public void shouldFailWithoutLoginTest() {
        String password = faker.internet().password();
        String firstName = faker.name().firstName();

        createCourierWithoutLogin(password, firstName);
    }


    @Test
    @DisplayName("Создание курьера без пароля — ошибка 400")
    @Story("Negative: Create courier without required fields")
    @Description("Создание курьера без пароля. Ожидается ошибка 400.")
    public void shouldFailWithoutPasswordTest() {
        testLogin = faker.name().username();
        String firstName = faker.name().firstName();

        CourierModel courier = new CourierModel();
        courier.setLogin(testLogin);
        courier.setPassword(null);
        courier.setFirstName(firstName);

        courierClient.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }


    @Test
    @DisplayName("Создание курьера без имени (firstName) — успешное создание")
    @Story("Positive: Create courier without optional fields")
    @Description("Создание курьера без имени. Ожидается успешный ответ, т.к. поле не является обязательным.")
    public void shouldCreateCourierWithoutFirstNameTest() {
        testLogin = faker.name().username();
        testPassword = faker.internet().password();

        CourierModel courier = new CourierModel();
        courier.setLogin(testLogin);
        courier.setPassword(testPassword);
        courier.setFirstName(null);

        courierClient.createCourier(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }


    @Step("Создание курьера: login={login}, password={password}, firstName={firstName}")
    private void createCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }

    @Step("Создание курьера повторно (ожидаем 409)")
    private void createCourierExpectingConflict(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName)
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Step("Создание курьера без логина")
    private void createCourierWithoutLogin(String password, String firstName) {
        courierClient.createCourier(null, password, firstName)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @After
    @Step("Удаление курьера после теста, если он был создан")
    public void cleanupCourier() {
        if (testLogin != null && testPassword != null) {
            try {
                int id = courierClient.loginCourier(testLogin, testPassword);
                courierClient.deleteCourier(id).statusCode(SC_OK);
            } catch (Exception ignored) {
            }
        }
    }
}
