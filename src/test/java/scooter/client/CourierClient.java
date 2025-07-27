package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import scooter.config.BaseSpec;
import scooter.model.CourierLoginModel;
import scooter.model.CourierModel;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class CourierClient {

    private static final String COURIER_ENDPOINT = "/api/v1/courier";

    @Step("Создание курьера: {0}")
    public ValidatableResponse createCourier(CourierModel courier) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body(courier)
                .when()
                .post(COURIER_ENDPOINT)
                .then();
    }

    @Step("Создание курьера: login={login}, password={password}, firstName={firstName}")
    public ValidatableResponse createCourier(String login, String password, String firstName) {
        CourierModel courier = new CourierModel();
        courier.setLogin(login);
        courier.setPassword(password);
        courier.setFirstName(firstName);
        return createCourier(courier);
    }

    @Step("Авторизация курьера: login = {0}")
    public int loginCourier(String login, String password) {
        CourierLoginModel loginModel = new CourierLoginModel(login, password);
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body(loginModel)
                .when()
                .post(COURIER_ENDPOINT + "/login")
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }


    @Step("Авторизация курьера (raw): login = {0}")
    public ValidatableResponse loginCourierRaw(String login, String password) {
        CourierLoginModel loginModel = new CourierLoginModel(login, password);
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body(loginModel)
                .when()
                .post(COURIER_ENDPOINT + "/login")
                .then();
    }

    @Step("Удаление курьера по id = {0}")
    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .when()
                .delete(COURIER_ENDPOINT + "/" + courierId)
                .then();
    }

    @Step("Удаление курьера без id")
    public ValidatableResponse deleteCourierWithoutId() {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .when()
                .delete(COURIER_ENDPOINT)
                .then();
    }
}
