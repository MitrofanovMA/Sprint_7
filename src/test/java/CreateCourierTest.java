import Couriers.Courier;
import Couriers.CourierCreate;
import Couriers.CourierDelete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.After;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest {
    private final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(URL.HOST)
            .addHeader("Content-Type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    private Courier courier;
    private int courierId = 0; // Храним id как int, а не Response
    private final CourierCreate courierCreate = new CourierCreate(baseRequestSpec);
    private final CourierDelete courierDelete = new CourierDelete(baseRequestSpec);

    @After
    @DisplayName("Очистка тестовых данных")
    public void tearDown() {
        if (courierId != 0) {
            courierDelete.delete(courierId);
            courierId = 0;
        }
    }

    @Test
    @DisplayName("Тест создания курьера")
    public void courierCanBeCreated() {
        courier = new Courier("testCourier_" + System.currentTimeMillis(),
                "password123", "Иван");

        Response response = courierCreate.create(courier);

        assertThat(response.statusCode())
                .as("Статус ответа должен быть 201 Created")
                .isEqualTo(201);

        assertThat(response.jsonPath().getBoolean("ok"))
                .as("Поле 'ok' в ответе должно быть true")
                .isTrue();
    }

    @Test
    @DisplayName("Тест на создание дубликата курьера")
    public void cannotCreateDuplicateCourier() {
        courier = new Courier("uniqueLogin_" + System.currentTimeMillis(),
                "password123", "Петр");

        // Создаём первого курьера (успешно)
        Response firstResponse = courierCreate.create(courier);
        assertThat(firstResponse.statusCode()).isEqualTo(201);// Сохраняем id

        // Пытаемся создать дубликат (ожидаем ошибку)
        courierCreate.create(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Тест успешного создания курьера со всеми полями")
    public void createCourierWithAllRequiredFieldsSuccess() {
        courier = new Courier("fullFields_" + System.currentTimeMillis(),
                "password123", "Сергей");

        Response response = courierCreate.create(courier)
                .then()
                .statusCode(201)
                .extract().response();

        assertThat(response.jsonPath().getBoolean("ok")).isTrue();
    }

    @Test
    @DisplayName("Тест создания курьера без логина")
    public void createCourierWithoutLoginReturnsError() {
        courier = new Courier(null, "password123", "Иван");

        courierCreate.create(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Тест создания курьера без пароля")
    public void createCourierWithoutPasswordReturnsError() {
        courier = new Courier("login123", null, "Иван");

        courierCreate.create(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
