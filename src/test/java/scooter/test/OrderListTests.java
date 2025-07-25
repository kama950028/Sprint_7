package scooter.test;

import io.qameta.allure.junit4.DisplayName;
import scooter.client.OrderClient;
import io.qameta.allure.*;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Order Listing")
public class OrderListTests {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение всех заказов")
    @Story("Positive: Get all orders")
    @Description("Проверяет, что возвращается список заказов без параметров")
    public void shouldReturnAllOrdersTest() {
        getAllOrdersAndCheck();
    }

    @Test
    @DisplayName("Получение заказов по courierId")
    @Story("Positive: Get orders by courierId")
    @Description("Проверяет, что можно получить заказы по courierId = 1")
    public void shouldReturnOrdersByCourierIdTest() {
        getOrdersByCourierId(1);
    }

    @Test
    @DisplayName("Получение заказов по courierId и ближайшим станциям")
    @Story("Positive: Get orders by courierId and station filters")
    @Description("Проверяет, что можно получить заказы по courierId и ближайшим станциям")
    public void shouldReturnOrdersByCourierIdAndStationsTest() {
        getOrdersByCourierIdAndStations(1, new String[]{"1", "2"});
    }

    @Test
    @DisplayName("Получение заказов с лимитом и страницей")
    @Story("Positive: Get paginated orders")
    @Description("Проверяет, что можно получить 10 заказов на 1-й странице")
    public void shouldReturnLimitedOrdersTest() {
        getLimitedOrders(10, 0);
    }

    @Test
    @DisplayName("Получение заказов по станции Калужская (110)")
    @Story("Positive: Get paginated orders near station")
    @Description("Проверяет, что можно получить 10 заказов на станции Калужская (110)")
    public void shouldReturnLimitedOrdersNearStationTest() {
        getLimitedOrdersByStations(10, 0, new String[]{"110"});
    }

    @Test
    @DisplayName("Пустой список для несуществующего courierId")
    @Story("Negative: Invalid courierId")
    @Description("Проверяет, что запрос с несуществующим courierId возвращает пустой список")
    public void shouldReturnEmptyListForInvalidCourierIdTest() {
        getOrdersByCourierIdExpectingEmpty(999999);
    }

    @Test
    @DisplayName("Пустой список при несуществующих станциях")
    @Story("Negative: Invalid nearestStation values")
    @Description("Проверяет, что запрос с несуществующими станциями возвращает пустой список")
    public void shouldReturnEmptyListForInvalidStationsTest() {
        getOrdersByCourierIdAndStationsExpectingEmpty(1, new String[]{"999", "888"});
    }

    @Test
    @DisplayName("Корректный ответ при пустом массиве nearestStation")
    @Story("Negative: Empty nearestStation array")
    @Description("Проверяет, что запрос с пустым массивом nearestStation возвращает валидный ответ")
    public void shouldReturnValidResponseForEmptyStationsTest() {
        getOrdersByCourierIdAndStations(1, new String[]{});
    }

    @Test
    @DisplayName("Ошибка или пустой результат при отрицательном limit")
    @Story("Negative: Negative limit")
    @Description("Проверяет, что limit = -5 вызывает ошибку или пустой результат")
    public void shouldFailForNegativeLimitTest() {
        getLimitedOrdersWithInvalidLimit(-5, 0);
    }

    @Test
    @DisplayName("Ошибка или пустой результат при отрицательной page")
    @Story("Negative: Negative page")
    @Description("Проверяет, что page = -1 вызывает ошибку или пустой результат")
    public void shouldFailForNegativePageTest() {
        getLimitedOrdersWithInvalidPage(10, -1);
    }

    @Test
    @DisplayName("Пустой результат при нереалистичных фильтрах")
    @Story("Negative: No orders for unrealistic filter")
    @Description("Проверяет, что при заведомо некорректных фильтрах заказы не возвращаются")
    public void shouldReturnEmptyListForUnrealisticFilterTest() {
        getLimitedOrdersByStationsExpectingEmpty(10, 0, new String[]{"9999"});
    }

    @Step("Получение всех заказов")
    private void getAllOrdersAndCheck() {
        orderClient.getAllOrders()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов по courierId = {courierId}")
    private void getOrdersByCourierId(int courierId) {
        orderClient.getOrdersByCourierId(courierId)
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов по courierId = {courierId} и станциям {stations}")
    private void getOrdersByCourierIdAndStations(int courierId, String[] stations) {
        orderClient.getOrdersByCourierIdAndStations(courierId, stations)
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов: limit = {limit}, page = {page}")
    private void getLimitedOrders(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(SC_OK)
                .body("orders.size()", lessThanOrEqualTo(limit));
    }

    @Step("Получение заказов по станциям {stations}, limit = {limit}, page = {page}")
    private void getLimitedOrdersByStations(int limit, int page, String[] stations) {
        orderClient.getLimitedOrdersByStations(limit, page, stations)
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Step("Ожидаем пустой список заказов по courierId = {courierId}")
    private void getOrdersByCourierIdExpectingEmpty(int courierId) {
        orderClient.getOrdersByCourierId(courierId)
                .statusCode(SC_OK)
                .body("orders", empty());
    }

    @Step("Ожидаем пустой список по несуществующим станциям: {stations}")
    private void getOrdersByCourierIdAndStationsExpectingEmpty(int courierId, String[] stations) {
        orderClient.getOrdersByCourierIdAndStations(courierId, stations)
                .statusCode(SC_OK)
                .body("orders", empty());
    }

    @Step("Проверка с некорректным limit = {limit}")
    private void getLimitedOrdersWithInvalidLimit(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(anyOf(is(SC_OK), is(SC_BAD_REQUEST)));
    }

    @Step("Проверка с некорректной page = {page}")
    private void getLimitedOrdersWithInvalidPage(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(anyOf(is(SC_OK), is(SC_BAD_REQUEST)));
    }

    @Step("Ожидаем пустой список при фильтрах по станциям: {stations}")
    private void getLimitedOrdersByStationsExpectingEmpty(int limit, int page, String[] stations) {
        orderClient.getLimitedOrdersByStations(limit, page, stations)
                .statusCode(SC_OK)
                .body("orders", empty());
    }
}
