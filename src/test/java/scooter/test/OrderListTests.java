package scooter.test;

import scooter.client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Order Listing")
public class OrderListTests {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @Story("Positive: Get all orders")
    @Description("Проверяет, что возвращается список заказов без параметров")
    public void shouldReturnAllOrdersTest() {
        getAllOrdersAndCheck();
    }

    @Test
    @Story("Positive: Get orders by courierId")
    @Description("Проверяет, что можно получить заказы по courierId = 1")
    public void shouldReturnOrdersByCourierIdTest() {
        getOrdersByCourierId(1);
    }

    @Test
    @Story("Positive: Get orders by courierId and station filters")
    @Description("Проверяет, что можно получить заказы по courierId и ближайшим станциям")
    public void shouldReturnOrdersByCourierIdAndStationsTest() {
        getOrdersByCourierIdAndStations(1, new String[]{"1", "2"});
    }

    @Test
    @Story("Positive: Get paginated orders")
    @Description("Проверяет, что можно получить 10 заказов на 1-й странице")
    public void shouldReturnLimitedOrdersTest() {
        getLimitedOrders(10, 0);
    }

    @Test
    @Story("Positive: Get paginated orders near station")
    @Description("Проверяет, что можно получить 10 заказов на станции Калужская (110)")
    public void shouldReturnLimitedOrdersNearStationTest() {
        getLimitedOrdersByStations(10, 0, new String[]{"110"});
    }

    @Test
    @Story("Negative: Invalid courierId")
    @Description("Проверяет, что запрос с несуществующим courierId возвращает пустой список")
    public void shouldReturnEmptyListForInvalidCourierIdTest() {
        getOrdersByCourierIdExpectingEmpty(999999);
    }

    @Test
    @Story("Negative: Invalid nearestStation values")
    @Description("Проверяет, что запрос с несуществующими станциями возвращает пустой список")
    public void shouldReturnEmptyListForInvalidStationsTest() {
        getOrdersByCourierIdAndStationsExpectingEmpty(1, new String[]{"999", "888"});
    }

    @Test
    @Story("Negative: Empty nearestStation array")
    @Description("Проверяет, что запрос с пустым массивом nearestStation возвращает валидный ответ")
    public void shouldReturnValidResponseForEmptyStationsTest() {
        getOrdersByCourierIdAndStations(1, new String[]{});
    }

    @Test
    @Story("Negative: Negative limit")
    @Description("Проверяет, что limit = -5 вызывает ошибку или пустой результат")
    public void shouldFailForNegativeLimitTest() {
        getLimitedOrdersWithInvalidLimit(-5, 0);
    }

    @Test
    @Story("Negative: Negative page")
    @Description("Проверяет, что page = -1 вызывает ошибку или пустой результат")
    public void shouldFailForNegativePageTest() {
        getLimitedOrdersWithInvalidPage(10, -1);
    }

    @Test
    @Story("Negative: No orders for unrealistic filter")
    @Description("Проверяет, что при заведомо некорректных фильтрах заказы не возвращаются")
    public void shouldReturnEmptyListForUnrealisticFilterTest() {
        getLimitedOrdersByStationsExpectingEmpty(10, 0, new String[]{"9999"});
    }

    @Step("Получение всех заказов")
    private void getAllOrdersAndCheck() {
        orderClient.getAllOrders()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов по courierId = {courierId}")
    private void getOrdersByCourierId(int courierId) {
        orderClient.getOrdersByCourierId(courierId)
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов по courierId = {courierId} и станциям {stations}")
    private void getOrdersByCourierIdAndStations(int courierId, String[] stations) {
        orderClient.getOrdersByCourierIdAndStations(courierId, stations)
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов с пагинацией: limit = {limit}, page = {page}")
    private void getLimitedOrders(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(200)
                .body("orders.size()", lessThanOrEqualTo(limit));
    }

    @Step("Получение заказов по станциям {stations}, limit = {limit}, page = {page}")
    private void getLimitedOrdersByStations(int limit, int page, String[] stations) {
        orderClient.getLimitedOrdersByStations(limit, page, stations)
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение заказов по courierId = {courierId}, ожидается пустой список")
    private void getOrdersByCourierIdExpectingEmpty(int courierId) {
        orderClient.getOrdersByCourierId(courierId)
                .statusCode(200)
                .body("orders", empty());
    }

    @Step("Получение заказов по courierId = {courierId} и несуществующим станциям")
    private void getOrdersByCourierIdAndStationsExpectingEmpty(int courierId, String[] stations) {
        orderClient.getOrdersByCourierIdAndStations(courierId, stations)
                .statusCode(200)
                .body("orders", empty());
    }

    @Step("Получение заказов с некорректным limit = {limit}")
    private void getLimitedOrdersWithInvalidLimit(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(anyOf(is(200), is(400)));
    }

    @Step("Получение заказов с некорректным page = {page}")
    private void getLimitedOrdersWithInvalidPage(int limit, int page) {
        orderClient.getLimitedOrders(limit, page)
                .statusCode(anyOf(is(200), is(400)));
    }

    @Step("Получение заказов с нереалистичными фильтрами (станции: {stations})")
    private void getLimitedOrdersByStationsExpectingEmpty(int limit, int page, String[] stations) {
        orderClient.getLimitedOrdersByStations(limit, page, stations)
                .statusCode(200)
                .body("orders", empty());
    }
}
