package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String COURIER_ENDPOINT = "/api/v1/courier";

    @Step("Создание курьера: login={0}")
    public ValidatableResponse createCourier(String login, String password, String firstName) {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(String.format("{ \"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\" }", login, password, firstName))
                .when()
                .post(COURIER_ENDPOINT)
                .then();
    }

    @Step("Авторизация курьера (raw): login = {0}")
    public ValidatableResponse loginCourierRaw(String login, String password) {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(String.format("{ \"login\": \"%s\", \"password\": \"%s\" }", login, password))
                .when()
                .post(COURIER_ENDPOINT + "/login")
                .then();
    }

    @Step("Авторизация курьера: login = {0}")
    public int loginCourier(String login, String password) {
        return loginCourierRaw(login, password)
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Step("Удаление курьера по id = {0}")
    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .baseUri(BASE_URI)
                .when()
                .delete(COURIER_ENDPOINT + "/" + courierId)
                .then();
    }

    @Step("Удаление курьера без id")
    public ValidatableResponse deleteCourierWithoutId() {
        return given()
                .baseUri(BASE_URI)
                .when()
                .delete(COURIER_ENDPOINT)
                .then();
    }
}
