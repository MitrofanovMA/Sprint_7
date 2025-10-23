package Couriers;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class CourierDelete {
    private final RequestSpecification baseRequestSpec;

    public CourierDelete(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public void delete(int id) {
        given()
                .spec(baseRequestSpec)
                .when()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200);
    }
}
