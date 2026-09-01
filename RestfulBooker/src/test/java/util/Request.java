package util;

import constants.RestfulBooksEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class Request {

    public static Response get(String endpoint) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        return RestAssured.when().get(endpoint);
    }

    // Usado para RB-8 y RB-9: filtrar el listado de reservas por query params
    // (ej. firstname, checkin, checkout)
    public static Response getWithQueryParams(String endpoint, Map<String, String> queryParams) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        return RestAssured.given().queryParams(queryParams)
                .when().get(endpoint);
    }

    public static Response getById(String endpoint, String id) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        return RestAssured.given().pathParam("id", id)
                .when().get(endpoint);
    }

    public static Response post(String endpoint, String payload) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        return RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload)
                .when().post(endpoint);
    }

    public static Response put(String endpoint, String id, String payload, String token) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        io.restassured.specification.RequestSpecification request =
                RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload)
                        .pathParam("id", id);
        if (token != null) {
            request = request.cookie("token", token);
        }
        return request.when().put(endpoint);
    }

    public static Response delete(String endpoint, String id, String token) {
        RestAssured.baseURI = RestfulBooksEndpoints.BASE_URL;
        io.restassured.specification.RequestSpecification request =
                RestAssured.given().pathParam("id", id);
        if (token != null) {
            request = request.cookie("token", token);
        }
        return request.when().delete(endpoint);
    }
}
