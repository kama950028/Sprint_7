package scooter.test;

import scooter.client.OrderClient;
import io.qameta.allure.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Get Order by Track")
public class OrderTrackTests {

    private final OrderClient orderClient = new OrderClient();
    private int createdTrack;

    @Before
    @Step("Создание заказа перед тестом")
    public void createOrder() {
        createdTrack = orderClient.createOrder(new String[]{"BLACK"})
                .statusCode(201)
                .extract()
                .path("track");
    }

    @Test
    @Story("Positive: Get order by valid track")
    @Description("Проверяет, что по корректному номеру заказа возвращается заказ")
    public void shouldReturnOrderByValidTrackTest() {
        getOrderByTrack(createdTrack);
    }

    @Test
    @Story("Negative: Missing track param")
    @Description("Проверяет, что без параметра track возвращается ошибка 400")
    public void shouldFailWithoutTrackParamTest() {
        getOrderWithoutTrack();
    }

    @Test
    @Story("Negative: Non-existent track")
    @Description("Проверяет, что с несуществующим треком возвращается ошибка 404")
    public void shouldFailForNonExistentTrackTest() {
        getOrderByInvalidTrack(9999999);
    }

    @After
    @Step("Завершение теста. Логирование track заказа")
    public void tearDown() {
        System.out.println("Тест завершён. Track заказа: " + createdTrack);
    }

    @Step("Получение заказа по треку: {0}")
    private void getOrderByTrack(int track) {
        orderClient.getOrderByTrack(track)
                .statusCode(200)
                .body("order", notNullValue());
    }

    @Step("Получение заказа по несуществующему треку: {0}")
    private void getOrderByInvalidTrack(int track) {
        orderClient.getOrderByTrack(track)
                .statusCode(404)
                .body("message", containsString("Заказ не найден"));
    }

    @Step("Попытка получить заказ без передачи track")
    private void getOrderWithoutTrack() {
        orderClient.getOrderWithoutTrack()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для поиска"));
    }
}
