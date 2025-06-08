package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class ApiClient {

    /**
     * Send an HTTP request to any endpoint with given parameters.
     *
     * @param method      HTTP method: GET, POST, PUT, DELETE, PATCH
     * @param endpoint    API endpoint, can include path variables like /books/{id}
     * @param headers     Optional headers (e.g., Content-Type, Authorization)
     * @param body        Optional request body for POST/PUT/PATCH
     * @param pathParams  Optional path parameters (e.g., id -> 5)
     * @param queryParams Optional query parameters (e.g., search -> "book")
     * @return Response from the API
     */
    public static Response sendRequest(
            String method,
            String endpoint,
            Map<String, String> headers,
            String body,
            Map<String, ?> pathParams,
            Map<String, ?> queryParams
    ) {
        RequestSpecification request = RestAssured.given().log().all();

        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }

        if (body != null && !body.isEmpty()) {
            request.body(body);
        }

        if (pathParams != null && !pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        Response response;
        switch (method.toUpperCase()) {
            case "GET":
                response = request.get(endpoint);
                break;
            case "POST":
                response = request.post(endpoint);
                break;
            case "PUT":
                response = request.put(endpoint);
                break;
            case "DELETE":
                response = request.delete(endpoint);
                break;
            case "PATCH":
                response = request.patch(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        response.then().log().all(); // Log response
        return response;
    }
}
