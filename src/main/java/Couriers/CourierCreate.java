package Couriers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class CourierCreate {

    private final RequestSpecification baseRequestSpec;

    public CourierCreate(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response create(Courier courier) {
        return given()
                .spec(baseRequestSpec)
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .extract().response();
    }
}
