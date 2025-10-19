import Orders.OrderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;

public class GetOrderListTest {

    private final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.HOST)
            .addHeader("Content-Type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    private final OrderClient orderClient = new OrderClient(baseRequestSpec);

    @Test
    @DisplayName("Проверка что возвращается список заказов")
    public void checkThatOrdersListIsReturned() {
        Response response = sendGetOrdersRequest();
        verifyOrdersListExists(response);
    }


    private Response sendGetOrdersRequest() {
        return orderClient.getOrdersList();
    }


    private void verifyOrdersListExists(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", instanceOf(List.class));

        response.jsonPath().getInt("orders[0].track"); // Проверяем что это список
    }

    @Test
    @DisplayName("Проверка что возвращается заказ по ID")
    public void checkOrderFromId(){

        Response getOrderId = orderClient.getOrdersList();
        Integer id = getOrderId.jsonPath().getInt("orders[0].track");

        Response response = orderClient.getOrderFromId(id);
        response.then().statusCode(200);
    }

    @Test
    @DisplayName("Получения заказа без номера в запросе")
    public void checkOrderWithoutId(){
        Response response = orderClient.getOrderWithoutId();
        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получения несуществующего заказа")
    public void checkOrderNonexistence(){
        Random random = new Random();
        int id = random.nextInt(10000000)+1;
        Response response = orderClient.getOrderFromId(id);
        response.then().statusCode(404).body("message", equalTo("Заказ не найден"));
    }
}
