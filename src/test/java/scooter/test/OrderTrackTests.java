package scooter.test;

import io.qameta.allure.junit4.DisplayName;
import scooter.client.OrderClient;
import io.qameta.allure.*;
import com.github.javafaker.Faker;
import org.junit.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Get Order by Track")
public class OrderTrackTests {

    private final OrderClient orderClient = new OrderClient();
    private final Faker faker = new Faker();
    private int createdTrack;

    @Before
    @Step("Создание заказа перед тестом")
    public void createOrder() {
        createdTrack = orderClient.createOrder(new String[]{"BLACK"})
                .statusCode(SC_CREATED)
                .extract()
                .path("track");
    }

    @Test
    @DisplayName("Получение заказа по корректному номеру трека")
    @Story("Positive: Get order by valid track")
    @Description("Проверяет, что по корректному номеру заказа возвращается заказ")
    public void shouldReturnOrderByValidTrackTest() {
        getOrderByTrack(createdTrack);
    }

    @Test
    @DisplayName("Ошибка при отсутствии параметра track")
    @Story("Negative: Missing track param")
    @Description("Проверяет, что без параметра track возвращается ошибка 400")
    public void shouldFailWithoutTrackParamTest() {
        getOrderWithoutTrack();
    }

    @Test
    @DisplayName("Ошибка при несуществующем треке заказа")
    @Story("Negative: Non-existent track")
    @Description("Проверяет, что с несуществующим треком возвращается ошибка 404")
    public void shouldFailForNonExistentTrackTest() {
        int invalidTrack = faker.number().numberBetween(10_000_000, 99_999_999); // реалистично-несуществующий
        getOrderByInvalidTrack(invalidTrack);
    }

    @After
    @Step("Отмена созданного заказа")
    public void tearDown() {
        if (createdTrack > 0) {
            orderClient.cancelOrderByTrack(createdTrack)
                    .statusCode(SC_OK)
                    .body("ok", is(true));
        }
    }

    @Step("Получение заказа по треку: {0}")
    private void getOrderByTrack(int track) {
        orderClient.getOrderByTrack(track)
                .statusCode(SC_OK)
                .body("order", notNullValue());
    }

    @Step("Получение заказа по несуществующему треку: {0}")
    private void getOrderByInvalidTrack(int track) {
        orderClient.getOrderByTrack(track)
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Заказ не найден"));
    }

    @Step("Попытка получить заказ без передачи track")
    private void getOrderWithoutTrack() {
        orderClient.getOrderWithoutTrack()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для поиска"));
    }
}
