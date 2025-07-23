package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDER_ENDPOINT = "/api/v1/orders";

    @Step("Создание заказа с цветами: {0}")
    public ValidatableResponse createOrder(String[] color) {
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body("{ \"color\": " + toJsonArray(color) + " }")
                .when()
                .post(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказа по треку: {0}")
    public ValidatableResponse getOrderByTrack(int track) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("t", track)
                .when()
                .get(ORDER_ENDPOINT + "/track")
                .then();
    }

    @Step("Получение заказа без передачи трека")
    public ValidatableResponse getOrderWithoutTrack() {
        return given()
                .baseUri(BASE_URI)
                .when()
                .get(ORDER_ENDPOINT + "/track")
                .then();
    }

    @Step("Получение всех заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .baseUri(BASE_URI)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов по courierId = {0}")
    public ValidatableResponse getOrdersByCourierId(int courierId) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("courierId", courierId)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов по courierId = {0} и станциям = {1}")
    public ValidatableResponse getOrdersByCourierIdAndStations(int courierId, String[] stations) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("courierId", courierId)
                .queryParam("nearestStation", stations)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов: limit = {0}, page = {1}")
    public ValidatableResponse getLimitedOrders(int limit, int page) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов: limit = {0}, page = {1}, stations = {2}")
    public ValidatableResponse getLimitedOrdersByStations(int limit, int page, String[] stations) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .queryParam("nearestStation", stations)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Принятие заказа: orderId = {0}, courierId = {1}")
    public ValidatableResponse acceptOrder(int orderId, int courierId) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("courierId", courierId)
                .when()
                .put(ORDER_ENDPOINT + "/accept/" + orderId)
                .then();
    }

    @Step("Принятие заказа без courierId: orderId = {0}")
    public ValidatableResponse acceptOrderWithoutCourierId(int orderId) {
        return given()
                .baseUri(BASE_URI)
                .when()
                .put(ORDER_ENDPOINT + "/accept/" + orderId)
                .then();
    }

    @Step("Принятие заказа без orderId (URL)")
    public ValidatableResponse acceptOrderWithoutOrderId(int courierId) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("courierId", courierId)
                .when()
                .put(ORDER_ENDPOINT + "/accept/")
                .then();
    }

    private static String toJsonArray(String[] colors) {
        if (colors == null || colors.length == 0) return "[]";
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < colors.length; i++) {
            json.append("\"").append(colors[i]).append("\"");
            if (i < colors.length - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}
