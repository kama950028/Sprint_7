package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import scooter.config.BaseSpec;
import scooter.model.OrderModel;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_ENDPOINT = "/api/v1/orders";

    @Step("Создание заказа с цветами: {0}")
    public ValidatableResponse createOrder(String[] color) {
        OrderModel order = OrderGenerator.generate(color);
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body(order)
                .when()
                .post(ORDER_ENDPOINT)
                .then();
    }


    @Step("Создание заказа (через модель)")
    public ValidatableResponse createOrder(OrderModel order) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body(order)
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .log().all();
    }

    @Step("Получение заказа по треку: {0}")
    public ValidatableResponse getOrderByTrack(int track) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("t", track)
                .when()
                .get(ORDER_ENDPOINT + "/track")
                .then();
    }

    @Step("Получение заказа без передачи трека")
    public ValidatableResponse getOrderWithoutTrack() {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .when()
                .get(ORDER_ENDPOINT + "/track")
                .then();
    }

    @Step("Получение всех заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов по courierId = {0}")
    public ValidatableResponse getOrdersByCourierId(int courierId) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("courierId", courierId)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов по courierId = {0} и станциям = {1}")
    public ValidatableResponse getOrdersByCourierIdAndStations(int courierId, String[] stations) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("courierId", courierId)
                .queryParam("nearestStation", stations)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов: limit = {0}, page = {1}")
    public ValidatableResponse getLimitedOrders(int limit, int page) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get(ORDER_ENDPOINT)
                .then();
    }

    @Step("Получение заказов: limit = {0}, page = {1}, stations = {2}")
    public ValidatableResponse getLimitedOrdersByStations(int limit, int page, String[] stations) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
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
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("courierId", courierId)
                .when()
                .put(ORDER_ENDPOINT + "/accept/" + orderId)
                .then();
    }

    @Step("Принятие заказа без courierId: orderId = {0}")
    public ValidatableResponse acceptOrderWithoutCourierId(int orderId) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .when()
                .put(ORDER_ENDPOINT + "/accept/" + orderId)
                .then();
    }

    @Step("Принятие заказа без orderId (URL)")
    public ValidatableResponse acceptOrderWithoutOrderId(int courierId) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .queryParam("courierId", courierId)
                .when()
                .put(ORDER_ENDPOINT + "/accept/")
                .then();
    }

    @Step("Отмена заказа по треку: {track}")
    public ValidatableResponse cancelOrderByTrack(int track) {
        return given()
                .spec(BaseSpec.REQUEST_SPEC)
                .body("{\"track\": " + track + "}")
                .when()
                .post("/api/v1/orders/cancel")
                .then();
    }




}
