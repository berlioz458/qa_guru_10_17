package in.reqres.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@Severity(SeverityLevel.CRITICAL)
public class ApiTests {

    String idx = "5";

    // GET /api/users/<id>
    // 1. success get by id = 10, check id +
    // 2. success get by id = 10, check email +
    // 3. not found error get by id = zero, big, negative +

    @Feature("Method Get User by id")
    @DisplayName("The test checks if the returned id is correct in the response query by id")
    @Test
    void checkIdInAnswerForQueryById() {
        Integer res =
                get("https://reqres.in/api/users/10")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("data.id");

        assertThat(res).isEqualTo(10);

    }

    @Feature("Method Get User by id")
    @DisplayName("The test checks if the returned email is correct in the response query by id")
    @Test
    void checkEmailInAnswerForQueryById() {
        String res =
                get("https://reqres.in/api/users/10")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("data.email");

        assertThat(res).isEqualTo("byron.fields@reqres.in");
    }

    @Feature("Method Get User by id")
    @ValueSource(strings = {"0", "1000000", "-666"})
    @ParameterizedTest(name = "The test checks for an error when queried for a non-existent id {0}")
    void foundErrorForQueryById(String id) {
        given().
                when().
                get("https://reqres.in/api/users/" + id)
                .then()
                .statusCode(404);
    }


    // POST /api/users with body - name and job
    // 1. success create with some test user's (params)
    // 2. ? with only name / only job - don't create


    public static Stream<Arguments> methodParams() {
        return Stream.of(
                Arguments.of("Petya", "QA Lead"),
                Arguments.of("Vova", "QA Automation"),
                Arguments.of("Liza", "QA Middle Manual"),
                Arguments.of("Jora", "QA Middle Manual")
        );
    }

    @Feature("Method Post User Create")
    @ParameterizedTest(name = "The test checks if the user was successfully created")
    @MethodSource("methodParams")
    void successCreateUser(String name, String job) {
        String body = "{ \"name\": \""+ name + "\", " +
                "\"job\": \"" + job + "\" }";
        given().
                contentType(JSON)
                .body(body)
                .when()
                .post("https://reqres.in/api/users")
                .then()
                .statusCode(201)
                .body("name", is(name))
                .body("job", is(job));
    }

    @Feature("Method Delete User")
    @Test
    void successDeleteUser() {
        given()
                .when()
                .delete("https://reqres.in/api/users/" + idx)// тут бы взять ид и так же параметризовать, но как сделать так чтобы тест бежал после выполнения POST-а, и вообще в конце всех тестов...???
                .then()
                .statusCode(204);
    }

    @Feature("Method PUT User")
    @Test
    void successPutUser() {
        String body = "{ \"name\": \"test\" }";
        given()
                .contentType(JSON)
                .body(body)
                .when()
                .put("https://reqres.in/api/users/10")
                .then()
                .statusCode(200)
                .body("name", is("test"));
    }



}
