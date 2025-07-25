package scooter.test;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import scooter.client.CourierClient;
import scooter.client.OrderClient;
import io.qameta.allure.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Accept Order")
@RunWith(Parameterized.class)
public class OrderAcceptTests {

    private final CourierClient courierClient = new CourierClient();
    private final OrderClient orderClient = new OrderClient();
    private final Faker faker = new Faker();

    private Integer courierId;

    private final int invalidCourierId;
    private final int invalidOrderId;

    public OrderAcceptTests(int invalidCourierId, int invalidOrderId) {
        this.invalidCourierId = invalidCourierId;
        this.invalidOrderId = invalidOrderId;
    }

    @Parameterized.Parameters(name = "Invalid courierId={0}, orderId={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1, -1},
                {0, 0},
                {999999, 999999},
        });
    }

    @Before
    @Step("Создание курьера перед тестом")
    public void createCourier() {
        String login = faker.name().username();
        String password = faker.internet().password();
        String firstName = faker.name().firstName();

        courierClient.createCourier(login, password, firstName)
                .statusCode(SC_CREATED);
        courierId = courierClient.loginCourier(login, password);
    }

    @After
    @Step("Удаление курьера после теста")
    public void deleteCourier() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId).statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Успешное принятие заказа курьером")
    @Story("Positive: Accept order successfully")
    @Description("Курьер успешно принимает заказ — переданы верные orderId и courierId через queryParam")
    public void shouldAcceptOrderSuccessfullyTest() {
        int orderId = createOrderAndGetId("BLACK");
        acceptOrder(orderId, courierId, SC_OK, true, null);
    }

    @Test
    @DisplayName("Ошибка: заказ уже принят")
    @Story("Negative: Accept already accepted order")
    @Description("Проверка: нельзя принять один и тот же заказ повторно")
    public void shouldFailToAcceptAlreadyAcceptedOrderTest() {
        int orderId = createOrderAndGetId("BLACK");
        acceptOrder(orderId, courierId, SC_OK, true, null);
        acceptOrder(orderId, courierId, SC_CONFLICT, false, "Этот заказ уже в работе");
    }

    @Test
    @DisplayName("Ошибка: неверные courierId и orderId")
    @Story("Negative: Invalid courierId or orderId")
    @Description("Переданы несуществующие orderId и courierId — ожидается ошибка 404")
    public void shouldFailWithInvalidIdsTest() {
        acceptOrder(invalidOrderId, invalidCourierId, SC_NOT_FOUND, false,
                "Курьера с таким id не существует");
    }

    @Test
    @DisplayName("Ошибка: не передан courierId")
    @Story("Negative: Missing courierId")
    @Description("Не передан courierId — запрос возвращает 400 и сообщение об ошибке")
    public void shouldFailWithoutCourierIdTest() {
        int orderId = createOrderAndGetId("GREY");

        orderClient.acceptOrderWithoutCourierId(orderId)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @DisplayName("Ошибка: не передан orderId в URL")
    @Story("Negative: Missing orderId (bad URL)")
    @Description("Не указан orderId в URL — ожидается ошибка 404")
    public void shouldFailWithoutOrderIdTest() {
        orderClient.acceptOrderWithoutOrderId(courierId)
                .statusCode(SC_NOT_FOUND);
    }

    @Step("Создание заказа с цветом: {0}")
    private int createOrderAndGetId(String color) {
        int track = orderClient.createOrder(new String[]{color})
                .statusCode(SC_CREATED)
                .extract()
                .path("track");

        return orderClient.getOrderByTrack(track)
                .statusCode(SC_OK)
                .extract()
                .path("order.id");
    }

    @Step("Принятие заказа: orderId={0}, courierId={1}, ожидаемый статус {2}")
    private void acceptOrder(int orderId, int courierId, int expectedStatus, boolean expectOk, String expectedMessage) {
        var response = orderClient.acceptOrder(orderId, courierId)
                .statusCode(expectedStatus);

        if (expectOk) {
            response.body("ok", is(true));
        }
        if (expectedMessage != null) {
            response.body("message", containsString(expectedMessage));
        }
    }
}
