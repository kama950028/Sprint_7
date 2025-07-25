package scooter.test;

import io.qameta.allure.junit4.DisplayName;
import scooter.client.OrderClient;
import io.qameta.allure.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import scooter.model.OrderModel;
import scooter.client.OrderGenerator;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Order API")
@Feature("Create Order")
@RunWith(Parameterized.class)
public class OrderTests {

    private final OrderClient orderClient = new OrderClient();
    private Integer createdTrack;

    @Parameterized.Parameter
    public String[] colors;

    @Parameterized.Parameters(name = "Colors: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}},
                {null}
        });
    }

    @Test
    @DisplayName("Create order with various color combinations")
    @Story("Positive: Create order with color variations")
    @Description("Создание заказа с параметризованными цветами. Проверка наличия track.")
    public void shouldCreateOrderWithColorsTest() {
        OrderModel order = OrderGenerator.generate(colors);
        createdTrack = orderClient.createOrder(order)
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract()
                .path("track");
    }

    @After
    @Step("Отмена заказа по треку: {createdTrack}")
    public void cancelOrderAfterTest() {
        if (createdTrack != null) {
            orderClient.cancelOrderByTrack(createdTrack)
                    .statusCode(SC_OK)
                    .body("ok", is(true));
        }
    }
}
