package Orders;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private final RequestSpecification baseRequestSpec;

    public OrderClient(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response createOrder(Order order) {
        return given()
                .spec(baseRequestSpec)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    // Метод для получения списка заказов без параметров
    public Response getOrdersList() {
        return given()
                .spec(baseRequestSpec)
                .when()
                .get("/api/v1/orders");
    }

    public Response getOrderFromId(int id) {
        return given()
                .spec(baseRequestSpec)
                .when()
                .get("/api/v1/orders/track?t=" + id);
    }

    public Response getOrderWithoutId() {
        return given()
                .spec(baseRequestSpec)
                .when()
                .get("/api/v1/orders/track?t=");
    }

}
