import Couriers.Courier;
import Couriers.CourierLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {
    private Courier courier;
    private int courierId;

    private final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.HOST)
            .addHeader("Content-Type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    private final CourierLogin courierLogin = new CourierLogin(baseRequestSpec);

    @Test
    @DisplayName("Курьер может авторизоваться + переданы все обязательные поля")
    public void courierCanBeAuth(){
        Response response = courierLogin.login("misha2401","1234");
        response.then().statusCode(200).extract().path("id");
    }

    @Test
    @DisplayName("Неправильно указан логин")
    public void courierAuthWithIncorrectLog(){
        Response response = courierLogin.login("0","1234");
        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Неправильно указан пароль")
    public void courierAuthWithIncorrectPass(){
        Response response = courierLogin.login("misha2401","0");
        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Нет поля Login")
    public void courierAuthWithoutLogin(){
        Response response = courierLogin.login(null, "1234");
        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нет поля Pass")
    public void courierAuthWithoutPass(){
        Response response = courierLogin.login("misha2401",null);
        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    public void courierAuthNonexistence(){
        Response response = courierLogin.login("123456789","123456789");
        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }




}



