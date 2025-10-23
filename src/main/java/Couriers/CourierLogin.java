package Couriers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class CourierLogin {

    private final RequestSpecification baseRequestSpec;

    public CourierLogin(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response login(String login, String password) {
        Courier requestBody = new Courier();
        requestBody.setLogin(login);
        requestBody.setPassword(password);

        return given()
                .spec(baseRequestSpec)
                .header("Content-Type", "application/json")
                .body(requestBody) // RestAssured сам сериализует в JSON
                .when()
                .post("/api/v1/courier/login");
    }
}
