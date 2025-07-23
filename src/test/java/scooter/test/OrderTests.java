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
@Feature("Create Order")
public class OrderTests {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @Story("Positive: Create order with one color")
    @Description("Создание заказа с одним цветом ('BLACK'). Ожидается успешный ответ и наличие track.")
    public void shouldCreateOrderWithOneColorTest() {
        createOrderAndCheckTrack(new String[]{"BLACK"});
    }

    @Test
    @Story("Positive: Create order with two colors")
    @Description("Создание заказа с двумя цветами ('BLACK', 'GREY'). Ожидается успешный ответ и track.")
    public void shouldCreateOrderWithTwoColorsTest() {
        createOrderAndCheckTrack(new String[]{"BLACK", "GREY"});
    }

    @Test
    @Story("Positive: Create order without color")
    @Description("Создание заказа без указания цвета. Проверка, что заказ создаётся и track возвращается.")
    public void shouldCreateOrderWithoutColorTest() {
        createOrderAndCheckTrack(new String[]{});
    }

    @Step("Создание заказа с цветами: {colors}")
    private void createOrderAndCheckTrack(String[] colors) {
        orderClient.createOrder(colors)
                .statusCode(201)
                .body("track", notNullValue());
    }
}
