import Orders.Order;
import Orders.OrderClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest {

    private final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.HOST)
            .addHeader("Content-Type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    private final OrderClient orderClient = new OrderClient(baseRequestSpec);

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("Создание заказа с цветом BLACK", new String[]{"BLACK"}),
                Arguments.of("Создание заказа с цветом GREY", new String[]{"GREY"}),
                Arguments.of("Создание заказа с обоими цветами", new String[]{"BLACK", "GREY"}),
                Arguments.of("Создание заказа без указания цвета", new String[]{})
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestData")
    @DisplayName("Создание заказа с разными цветами")
    public void createOrderWithDifferentColors(String testName, String[] colors) {
        createOrderWithColors(colors);
    }

    private void createOrderWithColors(String[] colors) {
        Order order = createTestOrder(colors);
        Response response = sendCreateOrderRequest(order);
        verifyOrderCreatedSuccessfully(response);
    }

    private Order createTestOrder(String[] colors) {
        return new Order(
                "Иван",
                "Иванов",
                "Москва, ул. Ленина, д. 1",
                "Сокольники",
                "+79999999999",
                3,
                "2024-12-31",
                "Тестовый комментарий",
                colors
        );
    }

    private Response sendCreateOrderRequest(Order order) {
        return orderClient.createOrder(order);
    }

    private void verifyOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", greaterThan(0));
    }
}